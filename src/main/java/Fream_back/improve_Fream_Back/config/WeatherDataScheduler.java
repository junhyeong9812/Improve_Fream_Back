package Fream_back.improve_Fream_Back.config;

import Fream_back.improve_Fream_Back.weather.service.WeatherDataCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherDataScheduler {
    private final WeatherDataCommandService weatherDataService;

    @Scheduled(fixedRate = 86400000) // 24시간마다 실행 (밀리초 단위)
    public void scheduleWeatherDataFetch() {
        weatherDataService.fetchAndStore24HourWeatherData();
    }
}

