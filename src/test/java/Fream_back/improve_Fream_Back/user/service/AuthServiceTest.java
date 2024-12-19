package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.dto.LoginRequestDto;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.redis.RedisService;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestConfig.class)
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RedisService redisService; // MockBean으로 변경

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @Autowired
    private User adminUser;

    @BeforeEach
    void setUp() {
        // 비밀번호 암호화
        user1.updatePassword(passwordEncoder.encode("password123"));
        user2.updatePassword(passwordEncoder.encode("password456"));
        adminUser.updatePassword(passwordEncoder.encode("adminpassword"));

        // 업데이트된 유저 저장
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(adminUser);
    }

    @Test
    @DisplayName("로그인 성공 - 일반 사용자")
    void testLoginSuccessUser() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("user1@example.com", "password123");

        // When
        String token = authService.login(loginRequestDto);

        // Then
        assertThat(token).isNotNull();
        verify(redisService, times(1)).addTokenToWhitelist(token);
    }

    @Test
    @DisplayName("로그인 성공 - 관리자")
    void testLoginSuccessAdmin() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("admin@example.com", "adminpassword");

        // When
        String token = authService.login(loginRequestDto);

        // Then
        assertThat(token).isNotNull();
        verify(redisService, times(1)).addTokenToWhitelist(token);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 이메일")
    void testLoginFailInvalidEmail() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("nonexistent@example.com", "password123");

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
        verify(redisService, never()).addTokenToWhitelist(anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void testLoginFailInvalidPassword() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("user1@example.com", "wrongpassword");

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
        verify(redisService, never()).addTokenToWhitelist(anyString());
    }
}

