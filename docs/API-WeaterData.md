# Weather Data 기능 정리

## 1. 기능 개요

- **Weather Data** 기능은 외부 API를 통해 날씨 데이터를 수집하고 저장하며, 사용자에게 특정 시간의 날씨 정보를 제공합니다.
- 실시간 데이터 요청 및 저장, 조회 API, 그리고 스케줄링 작업으로 구성됩니다.
- 추후 이 기능을 통해 날씨에 따라 상품 추천 기능을 추가할 예정
---

## 2. 주요 클래스 및 구성 요소

### 2.1 WeatherData Entity

- **위치:** `Fream_back.improve_Fream_Back.weather.entity`
- **목적:** 날씨 데이터를 저장하기 위한 엔티티.
- **주요 필드:**
    - `timestamp`: 데이터의 시간대.
    - `temperature`: 온도.
    - `precipitationProbability`: 강수 확률.
    - `precipitation`: 강수량.
    - `rain`: 비.
    - `snowfall`: 눈.
    - `retrievedAt`: 데이터가 저장된 시간.

```java
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private double temperature;
    private double precipitationProbability;
    private double precipitation;
    private double rain;
    private double snowfall;

    @Column(name = "retrieved_at")
    private LocalDateTime retrievedAt;
}
```

---

### 2.2 WeatherApiResponse DTO

- **위치:** `Fream_back.improve_Fream_Back.weather.dto`
- **목적:** 외부 날씨 API의 응답을 매핑하기 위한 DTO.
- **구성:**
    - `latitude`: 위도.
    - `longitude`: 경도.
    - `hourly`: 시간별 데이터.

```java
@Data
public class WeatherApiResponse {
    private double latitude;
    private double longitude;
    private Hourly hourly;

    @Data
    public static class Hourly {
        private List<String> time;
        private List<Double> temperature_2m;
        private List<Double> precipitation_probability;
        private List<Double> precipitation;
        private List<Double> rain;
        private List<Double> snowfall;
    }
}
```

---

### 2.3 WeatherDataDto

- **위치:** `Fream_back.improve_Fream_Back.weather.dto`
- **목적:** WeatherData Entity의 데이터를 클라이언트에 반환하기 위한 DTO.
- **구성:**
    - `timestamp`: 데이터의 시간대.
    - `temperature`: 온도.
    - `precipitationProbability`: 강수 확률.
    - `precipitation`: 강수량.
    - `rain`: 비.
    - `snowfall`: 눈.

```java
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDataDto {
    private LocalDateTime timestamp;
    private double temperature;
    private double precipitationProbability;
    private double precipitation;
    private double rain;
    private double snowfall;
}
```

---

### 2.4 WeatherDataCommandService

- **위치:** `Fream_back.improve_Fream_Back.weather.service`
- **목적:** 날씨 데이터를 외부 API에서 가져와 데이터베이스에 저장.
- **주요 기능:**
    - 외부 API에서 24시간의 데이터를 요청.
    - 중복된 `timestamp` 데이터는 저장하지 않음.

```java
@Service
@RequiredArgsConstructor
public class WeatherDataCommandService {
    private final WeatherDataRepository weatherDataRepository;
    private final RestTemplate restTemplate;

    private static final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=36.5&longitude=127.75&hourly=temperature_2m,precipitation_probability,precipitation,rain,snowfall&timezone=auto";

    public void fetchAndStore24HourWeatherData() {
        WeatherApiResponse response = restTemplate.getForObject(WEATHER_API_URL, WeatherApiResponse.class);

        if (response != null && response.getHourly() != null) {
            for (int i = 0; i < 24; i++) {
                LocalDateTime timestamp = LocalDateTime.parse(response.getHourly().getTime().get(i), DateTimeFormatter.ISO_DATE_TIME);

                if (!weatherDataRepository.existsByTimestamp(timestamp)) {
                    WeatherData weatherData = WeatherData.builder()
                            .timestamp(timestamp)
                            .temperature(response.getHourly().getTemperature_2m().get(i))
                            .precipitationProbability(response.getHourly().getPrecipitation_probability().get(i))
                            .precipitation(response.getHourly().getPrecipitation().get(i))
                            .rain(response.getHourly().getRain().get(i))
                            .snowfall(response.getHourly().getSnowfall().get(i))
                            .retrievedAt(LocalDateTime.now())
                            .build();

                    weatherDataRepository.save(weatherData);
                }
            }
        }
    }
}
```

---

### 2.5 WeatherQueryService

- **위치:** `Fream_back.improve_Fream_Back.weather.service`
- **목적:** 데이터베이스에 저장된 날씨 데이터를 클라이언트에 제공.
- **주요 기능:**
    - 현재 시간과 가장 가까운 데이터 반환.
    - 당일 데이터 반환 (시간 순으로 정렬).

```java
@Service
@RequiredArgsConstructor
public class WeatherQueryService {
    private final WeatherDataRepository weatherDataRepository;

    public Optional<WeatherDataDto> getClosestWeatherData() {
        LocalDateTime now = LocalDateTime.now();
        return weatherDataRepository.findClosestToTime(now)
                .map(this::convertToDto);
    }

    public List<WeatherDataDto> getTodayWeatherData() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        return weatherDataRepository.findByTimestampBetween(startOfDay, endOfDay).stream()
                .sorted((d1, d2) -> d1.getTimestamp().compareTo(d2.getTimestamp()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private WeatherDataDto convertToDto(WeatherData weatherData) {
        return WeatherDataDto.builder()
                .timestamp(weatherData.getTimestamp())
                .temperature(weatherData.getTemperature())
                .precipitationProbability(weatherData.getPrecipitationProbability())
                .precipitation(weatherData.getPrecipitation())
                .rain(weatherData.getRain())
                .snowfall(weatherData.getSnowfall())
                .build();
    }
}
```

---

### 2.6 WeatherQueryController

- **위치:** `Fream_back.improve_Fream_Back.weather.controller`
- **목적:** WeatherQueryService를 통해 날씨 데이터를 클라이언트에 제공합니다.
- **주요 API:**
    - `/api/weather/current`: 현재 시간과 가장 가까운 날씨 데이터.
    - `/api/weather/today`: 오늘 하루의 날씨 데이터.

```java
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherQueryController {
    private final WeatherQueryService weatherQueryService;

    @GetMapping("/current")
    public ResponseEntity<WeatherDataDto> getClosestWeatherData() {
        return weatherQueryService.getClosestWeatherData()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/today")
    public ResponseEntity<List<WeatherDataDto>> getTodayWeatherData() {
        List<WeatherDataDto> weatherDataList = weatherQueryService.getTodayWeatherData();
        return ResponseEntity.ok(weatherDataList);
    }
}
```

---

## 3. 파일 구조

```
Fream_back.improve_Fream_Back.weather
├── controller
│   └── WeatherQueryController.java
├── dto
│   ├── WeatherApiResponse.java
│   └── WeatherDataDto.java
├── entity
│   └── WeatherData.java
├── repository
│   └── WeatherDataRepository.java
├── service
│   ├── WeatherDataCommandService.java
│   └── WeatherQueryService.java
```

