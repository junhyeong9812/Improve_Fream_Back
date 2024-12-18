package Fream_back.improve_Fream_Back.notification.repository;

import Fream_back.improve_Fream_Back.notification.entity.Notification;
import Fream_back.improve_Fream_Back.notification.entity.NotificationCategory;
import Fream_back.improve_Fream_Back.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 이메일과 카테고리별 알림 조회 (최신순 정렬)
    @Query("SELECT n FROM Notification n JOIN FETCH n.user u " +
            "WHERE u.email = :email AND n.category = :category " +
            "ORDER BY n.createdDate DESC")
    List<Notification> findAllByUserEmailAndCategory(@Param("email") String email,
                                                     @Param("category") NotificationCategory category);

    // 이메일과 유형별 알림 조회 (최신순 정렬)
    @Query("SELECT n FROM Notification n JOIN FETCH n.user u " +
            "WHERE u.email = :email AND n.type = :type " +
            "ORDER BY n.createdDate DESC")
    List<Notification> findAllByUserEmailAndType(@Param("email") String email,
                                                 @Param("type") NotificationType type);

    // 읽음 여부와 카테고리별 알림 조회 (최신순 + 페이징)
    @Query("SELECT n FROM Notification n JOIN FETCH n.user u " +
            "WHERE u.email = :email AND n.isRead = :isRead AND n.category = :category " +
            "ORDER BY n.createdDate DESC")
    Page<Notification> findAllByUserEmailAndCategoryAndIsRead(
            @Param("email") String email,
            @Param("category") NotificationCategory category,
            @Param("isRead") boolean isRead,
            Pageable pageable);

    // 읽음 여부와 유형별 알림 조회 (최신순 + 페이징)
    @Query("SELECT n FROM Notification n JOIN FETCH n.user u " +
            "WHERE u.email = :email AND n.isRead = :isRead AND n.type = :type " +
            "ORDER BY n.createdDate DESC")
    Page<Notification> findAllByUserEmailAndTypeAndIsRead(
            @Param("email") String email,
            @Param("type") NotificationType type,
            @Param("isRead") boolean isRead,
            Pageable pageable);
}