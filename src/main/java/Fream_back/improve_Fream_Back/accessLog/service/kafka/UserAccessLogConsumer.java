package Fream_back.improve_Fream_Back.accessLog.service.kafka;

import Fream_back.improve_Fream_Back.accessLog.dto.UserAccessLogEvent;
import Fream_back.improve_Fream_Back.accessLog.entity.UserAccessLog;
import Fream_back.improve_Fream_Back.accessLog.repository.UserAccessLogRepository;
import Fream_back.improve_Fream_Back.accessLog.service.GeoIPService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserAccessLogConsumer {

    private final UserAccessLogRepository userAccessLogRepository;
    private final GeoIPService geoIPService;

    @KafkaListener(topics = "user-access-log-topic",
            groupId = "user-access-log-group",
            containerFactory = "userAccessLogKafkaListenerContainerFactory"
    )
    public void consume(UserAccessLogEvent event) {

        // (1) IP로부터 위치 정보 조회(Consumer에서 처리)
        GeoIPService.Location location = geoIPService.getLocation(event.getIpAddress());

        // (2) DB 엔티티 생성
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
                .email(event.getEmail() != null ? event.getEmail() : "Anonymous")
                .isAnonymous(event.isAnonymous())

                .networkType(event.getNetworkType())
                .browserLanguage(event.getBrowserLanguage())
                .screenWidth(event.getScreenWidth())
                .screenHeight(event.getScreenHeight())
                .devicePixelRatio(event.getDevicePixelRatio())

                .accessTime(
                        event.getAccessTime() != null
                                ? event.getAccessTime()
                                : LocalDateTime.now()
                )
                .build();

        // (3) DB 저장
        userAccessLogRepository.save(log);
    }
}
