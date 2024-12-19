package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.dto.PasswordResetRequestDto;
import Fream_back.improve_Fream_Back.user.entity.User;
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
class PasswordResetServiceTest {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService; // EmailService를 Mock 처리

    @Autowired
    private User user1;

    @BeforeEach
    void setUp() {
        user1.updatePassword(passwordEncoder.encode("password123"));
        userRepository.save(user1);
    }

    @Test
    @DisplayName("비밀번호 재설정 자격 확인 - 성공")
    void testCheckPasswordResetEligibilitySuccess() {
        // When
        boolean eligible = passwordResetService.checkPasswordResetEligibility(user1.getEmail(), user1.getPhoneNumber());

        // Then
        assertThat(eligible).isTrue();
    }

    @Test
    @DisplayName("비밀번호 재설정 자격 확인 - 실패")
    void testCheckPasswordResetEligibilityFailure() {
        // When
        boolean eligible = passwordResetService.checkPasswordResetEligibility("nonexistent@example.com", "010-1111-2222");

        // Then
        assertThat(eligible).isFalse();
    }

    @Test
    @DisplayName("비밀번호 재설정 - 성공")
    void testResetPasswordSuccess() {
        // Given
        PasswordResetRequestDto dto = new PasswordResetRequestDto(
                user1.getEmail(),
                user1.getPhoneNumber(),
                "newpassword123",
                "newpassword123"
        );

        // When
        boolean result = passwordResetService.resetPassword(dto);

        // Then
        assertThat(result).isTrue();
        User updatedUser = userRepository.findByEmail(user1.getEmail()).orElseThrow();
        assertThat(passwordEncoder.matches("newpassword123", updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 재설정 - 사용자 미존재")
    void testResetPasswordUserNotFound() {
        // Given
        PasswordResetRequestDto dto = new PasswordResetRequestDto(
                "nonexistent@example.com",
                "010-1111-2222",
                "newpassword123",
                "newpassword123"
        );

        // When & Then
        assertThatThrownBy(() -> passwordResetService.resetPassword(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 전송 - 성공")
    void testCheckPasswordResetAndSendEmailSuccess() {
        // Given
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString()); // 이메일 전송 Mock

        // 기존 비밀번호 저장
        User originalUser = userRepository.findByEmail(user1.getEmail()).orElseThrow();
        String originalPassword = originalUser.getPassword();

        // When
        boolean result = passwordResetService.checkPasswordResetAndSendEmail(user1.getEmail(), user1.getPhoneNumber());

        // Then
        assertThat(result).isTrue();
        verify(emailService, times(1)).sendEmail(eq(user1.getEmail()), eq("임시 비밀번호 안내"), anyString());
        User updatedUser = userRepository.findByEmail(user1.getEmail()).orElseThrow();

        // 비밀번호가 암호화된 상태로 업데이트되었는지 확인
        assertThat(updatedUser.getPassword()).isNotEqualTo(originalPassword); // 기존 비밀번호와 다른지 확인
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 전송 - 실패 (사용자 없음)")
    void testCheckPasswordResetAndSendEmailUserNotFound() {
        // When
        boolean result = passwordResetService.checkPasswordResetAndSendEmail("nonexistent@example.com", "010-1111-2222");

        // Then
        assertThat(result).isFalse();
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 전송 - 실패 (이메일 전송 오류)")
    void testCheckPasswordResetAndSendEmailFailure() {
        // Given
        doThrow(new RuntimeException("Email sending failed")).when(emailService).sendEmail(anyString(), anyString(), anyString());

        // When & Then
        assertThatThrownBy(() ->
                passwordResetService.checkPasswordResetAndSendEmail(user1.getEmail(), user1.getPhoneNumber()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이메일 전송 실패로 인해 비밀번호가 업데이트되지 않았습니다.");
    }
}
