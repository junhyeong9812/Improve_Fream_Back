package Fream_back.improve_Fream_Back.config;

import Fream_back.improve_Fream_Back.weather.service.WeatherDataCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherDataInitializer {
    private final WeatherDataCommandService weatherDataService;

    @EventListener(ApplicationReadyEvent.class) // 서버 시작 시 동작
    public void initializeWeatherData() {
        weatherDataService.fetchAndStore24HourWeatherData();
    }
}

