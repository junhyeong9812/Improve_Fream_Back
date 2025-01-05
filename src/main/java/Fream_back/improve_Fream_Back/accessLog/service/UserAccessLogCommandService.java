package Fream_back.improve_Fream_Back.accessLog.service;

import Fream_back.improve_Fream_Back.accessLog.dto.UserAccessLogDto;
import Fream_back.improve_Fream_Back.accessLog.entity.UserAccessLog;
import Fream_back.improve_Fream_Back.accessLog.repository.UserAccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserAccessLogCommandService {

    private final UserAccessLogRepository userAccessLogRepository;
    private final GeoIPService geoIPService;

    public void createAccessLog(UserAccessLogDto logDto) {
        // IP로부터 위치 정보 조회
        GeoIPService.Location location = geoIPService.getLocation(logDto.getIpAddress());

        // UserAccessLog 엔티티 생성 및 저장
        UserAccessLog log = UserAccessLog.builder()
                .refererUrl(logDto.getRefererUrl())
                .userAgent(logDto.getUserAgent())
                .os(logDto.getOs())
                .browser(logDto.getBrowser())
                .deviceType(logDto.getDeviceType())
                .ipAddress(logDto.getIpAddress())
                .country(location.getCountry()) // 위치 정보 설정
                .region(location.getRegion())
                .city(location.getCity())
                .pageUrl(logDto.getPageUrl())
                .email(logDto.getEmail() != null ? logDto.getEmail() : "Anonymous") // 이메일 설정
                .isAnonymous(logDto.isAnonymous())
                .networkType(logDto.getNetworkType())
                .browserLanguage(logDto.getBrowserLanguage())
                .screenWidth(logDto.getScreenWidth())
                .screenHeight(logDto.getScreenHeight())
                .devicePixelRatio(logDto.getDevicePixelRatio())
                .accessTime(LocalDateTime.now())
                .build();

        userAccessLogRepository.save(log);
    }
}
