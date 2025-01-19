package Fream_back.improve_Fream_Back.accessLog.service.kafka;

import Fream_back.improve_Fream_Back.accessLog.dto.UserAccessLogDto;
import Fream_back.improve_Fream_Back.accessLog.dto.UserAccessLogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * UserAccessLog를 Kafka로 전송하는 Producer
 */
@Service
@RequiredArgsConstructor
public class UserAccessLogProducer {

    private static final String TOPIC_NAME = "user-access-log-topic";
    private final KafkaTemplate<String, UserAccessLogEvent> kafkaTemplate;

    /**
     * Controller/Service 레이어에서 받은 UserAccessLogDto를
     * UserAccessLogEvent로 변환 후, Kafka에 전송
     */
    public void sendAccessLog(UserAccessLogDto dto) {

        // Dto → Event 변환
        UserAccessLogEvent event = new UserAccessLogEvent();
        event.setRefererUrl(dto.getRefererUrl());
        event.setUserAgent(dto.getUserAgent());
        event.setOs(dto.getOs());
        event.setBrowser(dto.getBrowser());
        event.setDeviceType(dto.getDeviceType());

        event.setIpAddress(dto.getIpAddress());
        // country/region/city는 Consumer에서 채울 예정이라면 null로
        event.setCountry(null);
        event.setRegion(null);
        event.setCity(null);

        event.setPageUrl(dto.getPageUrl());
        event.setEmail(dto.getEmail());
        event.setAnonymous(dto.isAnonymous());

        event.setNetworkType(dto.getNetworkType());
        event.setBrowserLanguage(dto.getBrowserLanguage());
        event.setScreenWidth(dto.getScreenWidth());
        event.setScreenHeight(dto.getScreenHeight());
        event.setDevicePixelRatio(dto.getDevicePixelRatio());

        // accessTime은 Producer 생성 시점(= Controller 요청 시점) 기준
        event.setAccessTime(LocalDateTime.now());

        // Kafka 전송
        kafkaTemplate.send(TOPIC_NAME, event);
    }
}
