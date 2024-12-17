package Fream_back.improve_Fream_Back.notification.dto;

import Fream_back.improve_Fream_Back.notification.entity.NotificationCategory;
import Fream_back.improve_Fream_Back.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class NotificationRequestDTO {
    private NotificationCategory category;
    private NotificationType type;
    private String message;
}
