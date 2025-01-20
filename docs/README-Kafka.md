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


### 3. ViewEvent (상품 상세 조회 이벤트)

#### 3.1. 이벤트 DTO
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewEvent {
    private Long productColorId;
    private String email;
    private Integer age;
    private Gender gender;
    private LocalDateTime viewedAt;
}
```
- **productColorId**: 조회된 상품 색상의 ID
- **email**: 사용자 이메일 (익명 시 "anonymous")
- **viewedAt**: 조회 시간 (Producer에서 LocalDateTime.now()로 설정)

#### 3.2. Kafka Config (ViewEventKafkaConfig)
```java
@Configuration
@EnableKafka
public class ViewEventKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, ViewEvent> viewEventProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ViewEvent> viewEventKafkaTemplate() {
        return new KafkaTemplate<>(viewEventProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, ViewEvent> viewEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<ViewEvent> deserializer = new JsonDeserializer<>(ViewEvent.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ViewEvent> viewEventKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ViewEvent>();
        factory.setConsumerFactory(viewEventConsumerFactory());
        return factory;
    }
}
```
- **ViewEvent 전용 ProducerFactory & ConsumerFactory**
- **토픽**: `view-log-topic`

#### 3.3. Producer (ViewEventProducer)
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
        kafkaTemplate.send(TOPIC_NAME, event);
    }
}
```
- **sendViewEvent**: Controller/Service에서 상품 조회가 발생할 때 호출
- **메시지 전송**: `view-log-topic` 토픽에 ViewEvent를 JSON 직렬화하여 전송

#### 3.4. Consumer (ViewEventConsumer)
```java
@Service
@RequiredArgsConstructor
public class ViewEventConsumer {

    private final ProductColorRepository productColorRepository;
    private final ProductColorViewLogRepository viewLogRepository;

    @KafkaListener(
        topics = "view-log-topic",
        groupId = "my-group",
        containerFactory = "viewEventKafkaListenerContainerFactory"
    )
    public void listen(ViewEvent event) {
        ProductColor productColor = productColorRepository.findById(event.getProductColorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 색상"));

        ProductColorViewLog viewLog = ProductColorViewLog.create(
                productColor,
                event.getEmail(),
                (event.getAge() == null) ? 0 : event.getAge(),
                event.getGender()
        );
        viewLog.addViewedAt(event.getViewedAt());

        viewLogRepository.save(viewLog);
    }
}
```
- **@KafkaListener**: `view-log-topic` 구독
- **DB Insert**: `ProductColorViewLogRepository.save(viewLog)`

---

### 4. UserAccessLogEvent (사용자 접속 로그)

#### 4.1. 이벤트 DTO
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccessLogEvent {
    private String refererUrl;
    private String userAgent;
    private String os;
    private String browser;
    private String deviceType;
    private String ipAddress;
    private String country;
    private String region;
    private String city;
    private String pageUrl;
    private String email;
    private boolean isAnonymous;
    private String networkType;
    private String browserLanguage;
    private int screenWidth;
    private int screenHeight;
    private float devicePixelRatio;
    private LocalDateTime accessTime;
}
```

#### 4.2. Kafka Config (UserAccessLogKafkaConfig)
```java
@Configuration
@EnableKafka
public class UserAccessLogKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, UserAccessLogEvent> userAccessLogProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, UserAccessLogEvent> userAccessLogKafkaTemplate() {
        return new KafkaTemplate<>(userAccessLogProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, UserAccessLogEvent> userAccessLogConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(UserAccessLogEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserAccessLogEvent> userAccessLogKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, UserAccessLogEvent>();
        factory.setConsumerFactory(userAccessLogConsumerFactory());
        return factory;
    }
}
```
- **UserAccessLogEvent 전용 Kafka 설정**
- **토픽**: `user-access-log-topic`

#### 4.3. Producer (UserAccessLogProducer)
```java
@Service
@RequiredArgsConstructor
public class UserAccessLogProducer {

    private static final String TOPIC_NAME = "user-access-log-topic";
    private final KafkaTemplate<String, UserAccessLogEvent> kafkaTemplate;

    public void sendAccessLog(UserAccessLogDto dto) {
        UserAccessLogEvent event = new UserAccessLogEvent(
                dto.getRefererUrl(),
                dto.getUserAgent(),
                dto.getOs(),
                dto.getBrowser(),
                dto.getDeviceType(),
                dto.getIpAddress(),
                null, null, null,
                dto.getPageUrl(),
                dto.getEmail(),
                dto.isAnonymous(),
                dto.getNetworkType(),
                dto.getBrowserLanguage(),
                dto.getScreenWidth(),
                dto.getScreenHeight(),
                dto.getDevicePixelRatio(),
                LocalDateTime.now()
        );
        kafkaTemplate.send(TOPIC_NAME, event);
    }
}
```

#### 4.4. Consumer (UserAccessLogConsumer)
```java
@Service
@RequiredArgsConstructor
public class UserAccessLogConsumer {

    private final UserAccessLogRepository userAccessLogRepository;
    private final GeoIPService geoIPService;

    @KafkaListener(
        topics = "user-access-log-topic",
        groupId = "user-access-log-group",
        containerFactory = "userAccessLogKafkaListenerContainerFactory"
    )
    public void consume(UserAccessLogEvent event) {
        GeoIPService.Location location = geoIPService.getLocation(event.getIpAddress());

        UserAccessLog log = UserAccessLog.builder()
                .refererUrl(event.getRefererUrl())
                .userAgent(event.getUserAgent())
                .os(event.getOs())
                .browser(event.getBrowser())
                .deviceType(event.getDeviceType())
                .ipAddress(event.getIpAddress())
                .country(location.getCountry())
                .region(location.getRegion())
                .city(location.getCity())
                .pageUrl(event.getPageUrl())
                .email(event.getEmail() == null ? "Anonymous" : event.getEmail())
                .isAnonymous(event.isAnonymous())
                .networkType(event.getNetworkType())
                .browserLanguage(event.getBrowserLanguage())
                .screenWidth(event.getScreenWidth())
                .screenHeight(event.getScreenHeight())
                .devicePixelRatio(event.getDevicePixelRatio())
                .accessTime(event.getAccessTime() != null ? event.getAccessTime() : LocalDateTime.now())
                .build();

        userAccessLogRepository.save(log);
    }
}
```

---

### 5. 정리 (코드 구조)
```plaintext
├─ config
│   ├─ kafka
│   │   ├─ ViewEventKafkaConfig.java        // ViewEvent 전용 Kafka 설정
│   │   └─ UserAccessLogKafkaConfig.java    // UserAccessLogEvent 전용 Kafka 설정
│
├─ product
│   ├─ dto.kafka
│   │   └─ ViewEvent.java                   // 뷰 이벤트 DTO
│   └─ service.kafka
│       ├─ ViewEventProducer.java           // 뷰 이벤트 Producer
│       └─ ViewEventConsumer.java           // 뷰 이벤트 Consumer
│
├─ accessLog
│   ├─ dto
│   │   └─ UserAccessLogEvent.java          // 접속 로그 이벤트 DTO
│   └─ service.kafka
│       ├─ UserAccessLogProducer.java       // 접속 로그 Producer
│       └─ UserAccessLogConsumer.java       // 접속 로그 Consumer
```
- **ViewEvent**와 **UserAccessLogEvent**를 각각 다른 Config로 분리
- **토픽**: `view-log-topic` / `user-access-log-topic`로 구분
- Producer/Consumer가 서로 다른 containerFactory를 사용

---

### 6. 사용 방식

#### 상품 상세 조회 시
- **ViewEventProducer.sendViewEvent(...) 호출**
  - → `view-log-topic`에 ViewEvent 전송
  - → ViewEventConsumer가 수신하여 DB(ProductColorViewLog) 저장

#### 사용자 접속 로그 시 (예: UserAccessLogCommandController)
- **UserAccessLogProducer.sendAccessLog(dto) 호출**
  - → `user-access-log-topic`에 UserAccessLogEvent 전송
  - → UserAccessLogConsumer가 수신 → DB(UserAccessLog) 저장

---

### 7. 배치 처리 (고트래픽 대응 시)
- 현재 예시 코드는 메시지 1건 소비 시 DB insert를 수행
- 트래픽이 매우 큰 경우, 컨슈머에서 버퍼에 쌓았다가 주기적(saveAll)으로 한 번에 저장 가능
- **ConcurrentKafkaListenerContainerFactory**에서 BatchListener 모드(`setBatchListener(true)`)를 쓸 수도 있음

---

### 8. 결론
- **ViewEvent**와 **UserAccessLogEvent**를 각각 별도의 Config와 Producer/Consumer로 운영
- 동일한 Kafka 브로커를 사용하지만, Topic과 DTO를 구분하여 충돌 없이 관리
- 이 구조를 통해 비동기 로그 처리와 확장성을 확보할 수 있음
- 필요 시, batch insert나 다른 토픽 추가(ex. 알림, 트래킹 등)로 확장 가능
- **장점**: 확장성(대규모 트래픽 처리), 비동기/실시간 분석 가능
- **차후 확장**:
    - “배치 처리”로 DB 부하 최적화
    - “다른 GroupID” 리스너를 추가해 중복 소비(알람/분석) 가능
    - “타입 변경”: 동일 아키텍처로 다양한 이벤트 전송/처리

