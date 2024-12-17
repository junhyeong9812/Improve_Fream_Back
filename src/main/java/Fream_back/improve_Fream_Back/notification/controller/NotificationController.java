package Fream_back.improve_Fream_Back.notification.controller;

import Fream_back.improve_Fream_Back.notification.dto.NotificationDTO;
import Fream_back.improve_Fream_Back.notification.dto.NotificationRequestDTO;
import Fream_back.improve_Fream_Back.notification.entity.Notification;
import Fream_back.improve_Fream_Back.notification.entity.NotificationCategory;
import Fream_back.improve_Fream_Back.notification.entity.NotificationType;
import Fream_back.improve_Fream_Back.notification.service.NotificationCommandService;
import Fream_back.improve_Fream_Back.notification.service.NotificationQueryService;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationCommandService commandService;
    private final NotificationQueryService queryService;
    private final RedisTemplate<String, String> redisTemplate; // RedisTemplate 주입 추가

    public NotificationController(NotificationCommandService commandService,
                                  NotificationQueryService queryService,
                                  RedisTemplate<String, String> redisTemplate) { // 생성자에서 주입
        this.commandService = commandService;
        this.queryService = queryService;
        this.redisTemplate = redisTemplate; // 주입된 RedisTemplate 저장
    }
    // SecurityContextHolder에서 이메일 추출
    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다."); // 인증 실패 처리
    }
    //단일 사용자 알림
    @PostMapping
    public NotificationDTO createNotification(@RequestParam Long userId, @RequestBody NotificationRequestDTO requestDTO) {
        return commandService.createNotification(userId, requestDTO.getCategory(), requestDTO.getType(), requestDTO.getMessage());
    }

    //전체 알림 생성
    @PostMapping("/broadcast")
    public List<NotificationDTO> createNotificationForAll(@RequestBody NotificationRequestDTO requestDTO) {
        return commandService.createNotificationForAll(requestDTO);
    }

    // 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        String email = extractEmailFromSecurityContext(); // SecurityContext에서 이메일 추출
        commandService.markAsRead(id, email); // 이메일과 알림 ID를 기반으로 처리
    }

    // 카테고리별 알림 조회
    @GetMapping("/filter/category")
    public List<NotificationDTO> filterByCategory(@RequestParam NotificationCategory category) {
        String email = extractEmailFromSecurityContext(); // 토큰에서 이메일 추출
        return queryService.filterByCategory(email, category);
    }

    // 유형별 알림 조회
    @GetMapping("/filter/type")
    public List<NotificationDTO> filterByType(@RequestParam NotificationType type) {
        String email = extractEmailFromSecurityContext(); // 토큰에서 이메일 추출
        return queryService.filterByType(email, type);
    }

    // 카테고리별 + 읽음 여부 조회
    @GetMapping("/filter/category/read-status")
    public List<NotificationDTO> filterByCategoryAndIsRead(
            @RequestParam NotificationCategory category,
            @RequestParam boolean isRead,
            @RequestParam int page,
            @RequestParam int size) {
        String email = extractEmailFromSecurityContext(); // 이메일 추출
        return queryService.filterByCategoryAndIsRead(email, category, isRead, PageRequest.of(page, size));
    }

    // 유형별 + 읽음 여부 조회
    @GetMapping("/filter/type/read-status")
    public List<NotificationDTO> filterByTypeAndIsRead(
            @RequestParam NotificationType type,
            @RequestParam boolean isRead,
            @RequestParam int page,
            @RequestParam int size) {
        String email = extractEmailFromSecurityContext(); // 이메일 추출
        return queryService.filterByTypeAndIsRead(email, type, isRead, PageRequest.of(page, size));
    }

    // PING 처리 (연결 상태 갱신)
    @MessageMapping("/ping")
    public void handlePing() {
        String email = extractEmailFromSecurityContext(); // SecurityContext에서 이메일 추출
        if (email != null) {
            String redisKey = "WebSocket:User:" + email; // Redis 키를 이메일 기반으로 설정

            // Redis에서 남은 TTL 확인 (밀리초 단위로 반환됨)
            Long remainingTime = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS); // 초 단위로 TTL 가져오기

            // TTL이 10분 이하인 경우에만 갱신
            if (remainingTime != null && remainingTime <= 600) { // 600초 == 10분
                redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES); // TTL 30분 연장
                System.out.println("Redis TTL 갱신: 사용자 Email = " + email + ", 남은 TTL = " + remainingTime + "초");
            } else {
                System.out.println("TTL 연장 불필요: 사용자 Email = " + email + ", 남은 TTL = " + remainingTime + "초");
            }
        }
    }
}


