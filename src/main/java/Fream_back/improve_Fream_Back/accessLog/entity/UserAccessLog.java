package Fream_back.improve_Fream_Back.accessLog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_access_log")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_access_log_seq")
    @SequenceGenerator(name = "user_access_log_seq", sequenceName = "USER_ACCESS_LOG_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "REFERER_URL")
    private String refererUrl; // 참조 URL

    @Column(name = "USER_AGENT")
    private String userAgent; // 브라우저 및 OS 정보

    private String os; // 운영체제 정보
    private String browser; // 브라우저 정보

    @Column(name = "DEVICE_TYPE")
    private String deviceType; // 디바이스 타입 (Mobile, Desktop 등)

    @Column(name = "ACCESS_TIME")
    private LocalDateTime accessTime = LocalDateTime.now(); // 접근 시간

    private String ipAddress; // 사용자 IP 주소
    private String country; // 위치 정보 - 나라
    private String region;  // 위치 정보 - 지역
    private String city;    // 위치 정보 - 도시

    private String pageUrl; // 방문한 페이지 URL
    private String email; // 사용자 이메일 (토큰 기반)
    @Column(name = "IS_ANONYMOUS", nullable = false)
    private boolean isAnonymous = true; // 기본값으로 익명 사용자로 설정

    private String networkType; // 네트워크 타입 (WiFi, LTE 등)
    private String browserLanguage; // 브라우저 언어

    // 화면 구성 데이터
    private int screenWidth;  // 전체 화면 너비
    private int screenHeight; // 전체 화면 높이
    private float devicePixelRatio; // 픽셀 밀도
}
