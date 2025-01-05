package Fream_back.improve_Fream_Back.accessLog.controller;

import Fream_back.improve_Fream_Back.accessLog.dto.UserAccessLogDto;
import Fream_back.improve_Fream_Back.accessLog.service.UserAccessLogCommandService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/access-log")
@RequiredArgsConstructor
public class UserAccessLogCommandController {

    private final UserAccessLogCommandService userAccessLogCommandService;

    @PostMapping("/create")
    public void createAccessLog(@RequestBody UserAccessLogDto logDto, HttpServletRequest request) {
        // IP 주소 처리
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        logDto.setIpAddress(clientIp);

        // User-Agent 처리
        String userAgent = request.getHeader("User-Agent");
        logDto.setUserAgent(userAgent);

        // Referer URL 처리
        String refererUrl = request.getHeader("Referer");
        logDto.setRefererUrl(refererUrl);

        // 이메일이 없으면 익명 사용자로 설정
        if (logDto.getEmail() == null || logDto.getEmail().isEmpty()) {
            logDto.setEmail("Anonymous");
            logDto.setAnonymous(true);
        }

        // 서비스에 로그 데이터 전달
        userAccessLogCommandService.createAccessLog(logDto);
    }
}
