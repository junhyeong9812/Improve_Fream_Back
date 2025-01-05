package Fream_back.improve_Fream_Back.weather.service;

import Fream_back.improve_Fream_Back.weather.dto.WeatherApiResponse;
import Fream_back.improve_Fream_Back.weather.entity.WeatherData;
import Fream_back.improve_Fream_Back.weather.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherDataCommandService {
    private final WeatherDataRepository weatherDataRepository;
    private final RestTemplate restTemplate;

    private static final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=36.5&longitude=127.75&hourly=temperature_2m,precipitation_probability,precipitation,rain,snowfall&timezone=auto";

    public void fetchAndStore24HourWeatherData() {
        WeatherApiResponse response = restTemplate.getForObject(WEATHER_API_URL, WeatherApiResponse.class);

        if (response != null && response.getHourly() != null) {
            for (int i = 0; i < 24; i++) { // 24시간 데이터
                LocalDateTime timestamp = LocalDateTime.parse(response.getHourly().getTime().get(i), DateTimeFormatter.ISO_DATE_TIME);

                // 중복 데이터 검사
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
