package Fream_back.improve_Fream_Back.weather.repository;

import Fream_back.improve_Fream_Back.weather.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    // 특정 시간대와 가장 가까운 데이터 찾기
    @Query("SELECT w FROM WeatherData w WHERE ABS(TIMESTAMPDIFF(SECOND, w.timestamp, :targetTime)) = (SELECT MIN(ABS(TIMESTAMPDIFF(SECOND, w.timestamp, :targetTime))) FROM WeatherData)")
    Optional<WeatherData> findClosestToTime(LocalDateTime targetTime);

    // 특정 날짜 범위 데이터 찾기
    List<WeatherData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    //중복검사
    boolean existsByTimestamp(LocalDateTime timestamp);

}
