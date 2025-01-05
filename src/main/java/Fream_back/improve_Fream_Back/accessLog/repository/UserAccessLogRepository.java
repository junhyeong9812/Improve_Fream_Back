package Fream_back.improve_Fream_Back.accessLog.repository;

import Fream_back.improve_Fream_Back.accessLog.entity.UserAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long> {
    // 이메일 또는 IP 주소로 기존 로그 여부 확인
    boolean existsByEmailOrIpAddress(String email, String ipAddress);
    List<UserAccessLog> findByIsAnonymous(boolean isAnonymous);
}
