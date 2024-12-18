package Fream_back.improve_Fream_Back.notification.dto;

import Fream_back.improve_Fream_Back.notification.entity.NotificationCategory;
import Fream_back.improve_Fream_Back.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDTO {
    private Long id; // 알림 ID
    private NotificationCategory category; // 상위 카테고리
    private NotificationType type; // 세부 유형
    private String message; // 알림 메시지
    private boolean isRead; // 읽음 여부
    private String createdAt; // 생성 시간 (ISO 8601 형식)
}