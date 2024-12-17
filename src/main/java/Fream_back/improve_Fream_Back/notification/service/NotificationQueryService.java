package Fream_back.improve_Fream_Back.notification.service;

import Fream_back.improve_Fream_Back.notification.dto.NotificationDTO;
import Fream_back.improve_Fream_Back.notification.entity.Notification;
import Fream_back.improve_Fream_Back.notification.entity.NotificationCategory;
import Fream_back.improve_Fream_Back.notification.entity.NotificationType;
import Fream_back.improve_Fream_Back.notification.repository.NotificationRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 카테고리별 알림 조회
    public List<NotificationDTO> filterByCategory(String email, NotificationCategory category) {
        return notificationRepository.findAllByUserEmailAndCategory(email, category).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 유형별 알림 조회
    public List<NotificationDTO> filterByType(String email, NotificationType type) {
        return notificationRepository.findAllByUserEmailAndType(email, type).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    // 카테고리별 + 읽음 여부 알림 조회
    public List<NotificationDTO> filterByCategoryAndIsRead(String email, NotificationCategory category, boolean isRead, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAllByUserEmailAndCategoryAndIsRead(email, category, isRead, pageable);
        return notifications.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // 유형별 + 읽음 여부 알림 조회
    public List<NotificationDTO> filterByTypeAndIsRead(String email, NotificationType type, boolean isRead, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAllByUserEmailAndTypeAndIsRead(email, type, isRead, pageable);
        return notifications.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // DTO 변환 메서드
    private NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .category(notification.getCategory())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedDate().toString())
                .build();
    }
}

