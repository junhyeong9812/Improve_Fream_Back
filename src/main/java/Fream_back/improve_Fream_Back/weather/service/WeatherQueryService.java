package Fream_back.improve_Fream_Back.weather.service;

import Fream_back.improve_Fream_Back.weather.dto.WeatherDataDto;
import Fream_back.improve_Fream_Back.weather.entity.WeatherData;
import Fream_back.improve_Fream_Back.weather.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherQueryService {
    private final WeatherDataRepository weatherDataRepository;

    // 현재 시간과 가장 가까운 날씨 정보를 DTO로 반환
    public Optional<WeatherDataDto> getClosestWeatherData() {
        LocalDateTime now = LocalDateTime.now();
        return weatherDataRepository.findClosestToTime(now)
                .map(this::convertToDto);
    }

    // 당일의 모든 날씨 데이터를 정렬하여 DTO로 반환
    public List<WeatherDataDto> getTodayWeatherData() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        return weatherDataRepository.findByTimestampBetween(startOfDay, endOfDay).stream()
                .sorted((d1, d2) -> d1.getTimestamp().compareTo(d2.getTimestamp())) // 시간 오름차순 정렬
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // WeatherData를 WeatherDataDto로 변환하는 메서드
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
