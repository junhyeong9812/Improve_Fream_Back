package Fream_back.improve_Fream_Back.weather.controller;

import Fream_back.improve_Fream_Back.weather.dto.WeatherDataDto;
import Fream_back.improve_Fream_Back.weather.service.WeatherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherQueryController {
    private final WeatherQueryService weatherQueryService;

    // 현재 시간과 가장 가까운 날씨 정보 반환
    @GetMapping("/current")
    public ResponseEntity<WeatherDataDto> getClosestWeatherData() {
        return weatherQueryService.getClosestWeatherData()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 당일의 모든 날씨 데이터 반환
    @GetMapping("/today")
    public ResponseEntity<List<WeatherDataDto>> getTodayWeatherData() {
        List<WeatherDataDto> weatherDataList = weatherQueryService.getTodayWeatherData();
        return ResponseEntity.ok(weatherDataList);
    }
}
