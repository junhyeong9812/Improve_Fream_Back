## README-Kafka

### 개요
본 문서는 프로젝트 내 카프카(Kafka) 기능을 설명합니다. 조회(뷰) 이벤트를 카프카로 전달하고, 이를 소비(Consume)하여 DB에 저장하는 흐름을 살펴볼 수 있습니다.

### 1. Kafka 흐름 요약
1. Controller가 “상품 상세 조회” 시, Producer에게 ViewEvent 메시지를 보냄
2. Producer는 해당 메시지를 Kafka 토픽으로 전송
3. Consumer는 토픽을 구독하며 ViewEvent를 수신
4. Consumer가 ViewEvent의 내용을 DB에 저장(실시간 로그 기록)

이 과정을 통해 사용자의 상품 상세 페이지 조회 정보를 비동기로 처리하고, 확장성 있는 로깅/분석 기반을 마련합니다.

---

### 2. Kafka 설정

#### 2.1. application.yml 예시
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092  # (도커-compose 등 환경에 따라 변경)
    consumer:
      group-id: "my-group"
      auto-offset-reset: "earliest"
```
- `bootstrap-servers`: Kafka 브로커 주소 (로컬/도커 IP)
- `consumer.group-id`: 같은 그룹 ID를 갖는 Consumer끼리는 메시지를 파티션 단위로 분산 소비
- `auto-offset-reset`: 초기 구독 시 `earliest`로 설정 → 처음부터 메시지 읽기

#### 2.2. KafkaConfig.java
```java
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Producer Factory
    @Bean
    public ProducerFactory<String, ViewEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ViewEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Consumer Factory
    @Bean
    public ConsumerFactory<String, ViewEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(ViewEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ViewEvent> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ViewEvent>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
```
- **ProducerFactory**: ViewEvent를 JSON 직렬화(JsonSerializer)하여 전송
- **ConsumerFactory**: ViewEvent를 JSON 역직렬화(JsonDeserializer)로 수신
- **kafkaListenerContainerFactory**: `@KafkaListener`로 메시지 소비 시 동작하는 컨테이너 팩토리

---

### 3. Producer 코드
```java
@Service
@RequiredArgsConstructor
public class ViewEventProducer {
    private final KafkaTemplate<String, ViewEvent> kafkaTemplate;
    private static final String TOPIC_NAME = "view-log-topic";

    public void sendViewEvent(Long productColorId, String email, Integer age, Gender gender) {
        ViewEvent event = new ViewEvent(
                productColorId,
                email,
                age,
                gender,
                LocalDateTime.now()
        );
        kafkaTemplate.send(TOPIC_NAME, event); // Kafka 토픽으로 메시지 발행
    }
}
```
- **ViewEventProducer**: “상품 상세 조회” 시 호출
- **sendViewEvent**: ViewEvent 객체를 생성해 Kafka 토픽("view-log-topic")으로 전송

---

### 4. Consumer 코드
```java
@Service
@RequiredArgsConstructor
public class ViewEventConsumer {

    private final ProductColorRepository productColorRepository;
    private final ProductColorViewLogRepository viewLogRepository;

    @KafkaListener(topics = "view-log-topic", groupId = "my-group")
    public void listen(ViewEvent event) {
        // 메시지 수신: ViewEvent 정보를 바탕으로 DB에 로그 기록
        ProductColor productColor = productColorRepository.findById(event.getProductColorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 색상"));

        ProductColorViewLog viewLog = ProductColorViewLog.create(
                productColor,
                event.getEmail(),
                (event.getAge() == null) ? 0 : event.getAge(),
                event.getGender()
        );
        viewLog.addViewedAt(event.getViewedAt()); // 메시지의 viewedAt으로 덮어쓰기

        viewLogRepository.save(viewLog); // DB에 인서트
    }
}
```
- **ViewEventConsumer**: `@KafkaListener`로 "view-log-topic"을 구독
- **listen(...)**: ViewEvent 메시지를 DB 엔티티로 변환하여 저장

---

### 5. ViewEvent DTO
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewEvent {
    private Long productColorId;    // 조회된 상품 색상 ID
    private String email;           // 사용자 이메일(익명 시 "anonymous")
    private Integer age;            // 사용자 나이(없으면 0)
    private Gender gender;          // 사용자 성별(없으면 OTHER)
    private LocalDateTime viewedAt; // 조회 시각
}
```
- **ViewEvent**: 카프카 메시지로 주고받을 데이터 구조
- JSON 직렬화/역직렬화를 통해 Producer ↔ Consumer 간 전송

---

### 6. 동작 예시 (Controller → Producer → Consumer → DB)
```java
@GetMapping("/{productId}/detail")
public ResponseEntity<ProductDetailResponseDto> getProductDetail(
        @PathVariable("productId") Long productId,
        @RequestParam("colorName") String colorName
) {
    // 1) 상품 상세
    ProductDetailResponseDto detailDto = productQueryService.getProductDetail(productId, colorName);

    // 2) 로그인 사용자 이메일 추출 (없으면 anonymous)
    String email = SecurityUtils.extractEmailOrAnonymous();
    Integer age = 0;
    Gender gender = Gender.OTHER;

    // 3) Producer: 카프카 이벤트 발행
    viewEventProducer.sendViewEvent(
            detailDto.getColorId(),
            email,
            age,
            gender
    );

    // 4) 응답
    return ResponseEntity.ok(detailDto);
}
```
- **Controller**에서 상품 상세 조회 후 `viewEventProducer.sendViewEvent()` 호출
- Producer: "view-log-topic"에 ViewEvent 메시지 전송
- Consumer: 수신 → DB Insert
- DB에 뷰 로그 기록이 쌓임

---

### 7. 확장: 배치 처리 & 트래픽 이슈
현재는 “메시지를 한 개씩 받고, 즉시 DB Insert” 구조입니다. 고트래픽 시, DB 부하를 줄이기 위해 다음 두 가지 방법을 고려할 수 있습니다:
1. **Spring Kafka BatchListener 모드**: 한 번에 여러 메시지를 가져와 `saveAll()`
2. **내부 버퍼링**: List에 메시지를 쌓았다가 FLUSH_SIZE마다 Insert

---

### 결론
본 문서에서 카프카(Kafka) 환경 설정, Producer/Consumer 코드, 그리고 ViewEvent를 통한 비동기 뷰 로그 저장 과정을 정리했습니다.

- **장점**: 확장성(대규모 트래픽 처리), 비동기/실시간 분석 가능
- **차후 확장**:
    - “배치 처리”로 DB 부하 최적화
    - “다른 GroupID” 리스너를 추가해 중복 소비(알람/분석) 가능
    - “타입 변경”: 동일 아키텍처로 다양한 이벤트 전송/처리

