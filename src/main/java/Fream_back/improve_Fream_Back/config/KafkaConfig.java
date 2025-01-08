package Fream_back.improve_Fream_Back.config;

import Fream_back.improve_Fream_Back.product.dto.kafka.ViewEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    /**
     * application.yml (또는 properties)에서
     * spring.kafka.bootstrap-servers 값을 읽어온다.
     * 예: "localhost:9092" or "kafka:9092" (docker-compose 환경 등)
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // =============================================================================
    // Producer 설정
    // =============================================================================
    /**
     * Kafka Producer를 생성하는 Factory Bean.
     * - ViewEvent를 JSON 직렬화하여 전송하기 위한 설정.
     * - key는 String, value는 ViewEvent
     */
    @Bean
    public ProducerFactory<String, ViewEvent> producerFactory() {
        // Kafka Producer 설정값을 담는 Map
        Map<String, Object> props = new HashMap<>();
        // 1) 브로커 접속 정보
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // 2) 직렬화 설정
        //    - key : String
        //    - value : JSON (ViewEvent)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // DefaultKafkaProducerFactory에 props 주입
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * KafkaTemplate은 Producer를 사용해 메시지를 보내는 핵심 Bean.
     * - 이 Bean을 주입받아, 실제로 send() 등을 호출하게 됨.
     */
    @Bean
    public KafkaTemplate<String, ViewEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    // =============================================================================
    // Consumer 설정
    // =============================================================================
    /**
     * Kafka Consumer를 생성하는 Factory Bean.
     * - ViewEvent를 JSON 역직렬화하여 수신하기 위한 설정.
     * - key는 String, value는 ViewEvent
     */
    @Bean
    public ConsumerFactory<String, ViewEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 1) 브로커 접속 정보
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // 2) group.id 설정 (동일한 group id를 사용하는 consumer끼리는 메시지 파티션을 나눠서 처리)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");

        // 3) 역직렬화 설정
        //    - key : String
        //    - value : JSON (ViewEvent)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // DefaultKafkaConsumerFactory에 props + Deserializer 주입
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),                 // key deserializer
                new JsonDeserializer<>(ViewEvent.class)   // value deserializer (ViewEvent)
        );
    }

    /**
     * ConcurrentKafkaListenerContainerFactory:
     * @KafkaListener가 붙은 메서드에서 메시지를 비동기로 받도록 구성하는 Bean.
     * - consumerFactory()를 통해 Consumer를 생성.
     * - 배치 모드 설정 시, setBatchListener(true) 등을 추가할 수도 있음.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ViewEvent> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ViewEvent>();
        factory.setConsumerFactory(consumerFactory());
        // 필요 시 factory.setBatchListener(true) 등 설정 가능
        return factory;
    }
}

