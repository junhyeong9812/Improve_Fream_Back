package Fream_back.improve_Fream_Back.shipment.config;

import Fream_back.improve_Fream_Back.notification.service.NotificationCommandService;
import Fream_back.improve_Fream_Back.shipment.entity.OrderShipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import Fream_back.improve_Fream_Back.shipment.repository.OrderShipmentRepository;
import Fream_back.improve_Fream_Back.shipment.service.OrderShipmentCommandService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;



@Configuration
@RequiredArgsConstructor
public class UpdateShipmentStatusesJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    private final OrderShipmentRepository orderShipmentRepository;
    private final NotificationCommandService notificationService;
    private final OrderShipmentCommandService orderService;

    @Bean
    public Job updateShipmentStatusesJob() {
        return new JobBuilder("updateShipmentStatusesJob", jobRepository)
                .start(updateShipmentStatusesStep())
                .build();
    }

    @Bean
    public Step updateShipmentStatusesStep() {
        return new StepBuilder("updateShipmentStatusesStep", jobRepository)
                .<OrderShipment, OrderShipment>chunk(50, transactionManager)
                .reader(shipmentItemReader())    // 1) 읽기
                .processor(shipmentItemProcessor()) // 2) 처리(스크래핑 + 상태 업데이트)
                .writer(shipmentJpaItemWriter())    // 3) 쓰기(DB 반영)
                .faultTolerant()
                .skip(Exception.class)           // 네트워크/파싱 등 예외 시 Skip
                .skipLimit(50)
                .listener(shipmentSkipListener())
                .build();
    }

    /**
     * 1) Reader: 상태가 IN_TRANSIT 또는 OUT_FOR_DELIVERY 인 OrderShipment 목록을 페이지 단위로 읽음
     */
    @Bean
    @StepScope
    public JpaPagingItemReader<OrderShipment> shipmentItemReader() {
        return new JpaPagingItemReaderBuilder<OrderShipment>()
                .name("shipmentItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(50)
                .queryString("SELECT s FROM OrderShipment s WHERE s.status IN ('IN_TRANSIT','OUT_FOR_DELIVERY')")
                .build();
    }

    /**
     * 2) Processor: 각 OrderShipment에 대해 CJ대한통운 페이지를 조회하여 상태 갱신
     *    - parseCjLogisticsStatus(...) 를 통해 배송 상태 파싱
     *    - DELIVERY 완료 시 알림, Order 상태 완료 등도 여기서 처리 가능
     */
    @Bean
    @StepScope
    public ItemProcessor<OrderShipment, OrderShipment> shipmentItemProcessor() {

        // 만약 Processor를 여러 단계로 나누고 싶다면 CompositeItemProcessorBuilder 사용도 가능
        return orderShipment -> {
            // 2-1) Jsoup 스크래핑으로 현재 상태 가져오기
            String currentStatus = parseCjLogisticsStatus(orderShipment.getTrackingNumber());

            // 2-2) 매핑
            ShipmentStatus newStatus = mapToShipmentStatus(currentStatus);

            // 2-3) 상태가 DELIVERED 라면,
            if (newStatus == ShipmentStatus.DELIVERED) {
                orderShipment.updateStatus(ShipmentStatus.DELIVERED);
                // Order 상태도 COMPLETED 로 변경
                orderService.completeOrder(orderShipment.getOrder().getId());
                // 알림 발송
                notificationService.notifyShipmentCompleted(orderShipment.getOrder());
            }
            else if (newStatus == ShipmentStatus.OUT_FOR_DELIVERY) {
                orderShipment.updateStatus(ShipmentStatus.OUT_FOR_DELIVERY);
                // 필요시 알림, 로직 추가
            }
            else {
                orderShipment.updateStatus(ShipmentStatus.IN_TRANSIT);
            }

            return orderShipment; // Writer로 넘김
        };
    }

    private String parseCjLogisticsStatus(String trackingNumber) throws Exception {
        // 예시: 로직 단순화
        String url = "https://trace.cjlogistics.com/next/tracking.html?wblNo=" + trackingNumber;
        Document doc = Jsoup.connect(url).get();
        // tbody#statusDetail 의 마지막 tr => 5번째 td
        Elements rows = doc.select("tbody#statusDetail tr");
        if (rows.isEmpty()) {
            throw new IllegalStateException("배송 정보가 없습니다. trackingNo=" + trackingNumber);
        }
        return rows.last().select("td").get(4).text();
    }

    private ShipmentStatus mapToShipmentStatus(String statusText) {
        return switch (statusText) {
            case "배송완료" -> ShipmentStatus.DELIVERED;
            case "배송출발" -> ShipmentStatus.OUT_FOR_DELIVERY;
            default -> ShipmentStatus.IN_TRANSIT;
        };
    }

    /**
     * 3) Writer: DB에 반영 (상태 갱신)
     */
    @Bean
    @StepScope
    public JpaItemWriter<OrderShipment> shipmentJpaItemWriter() {
        JpaItemWriter<OrderShipment> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    /**
     * SkipListener: 예외 발생 시 로그
     */
    @Bean
    public SkipListener<OrderShipment, OrderShipment> shipmentSkipListener() {
        return new SkipListener<>() {
            @Override
            public void onSkipInProcess(OrderShipment item, Throwable t) {
                System.err.println("[Skip] ShipmentID: " + item.getId() + " reason=" + t.getMessage());
            }
            @Override
            public void onSkipInRead(Throwable t) {
                System.err.println("[SkipRead] " + t.getMessage());
            }
            @Override
            public void onSkipInWrite(OrderShipment item, Throwable t) {
                System.err.println("[SkipWrite] " + item.getId() + " reason=" + t.getMessage());
            }
        };
    }
}
