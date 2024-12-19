package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.dto.LoginInfoUpdateDto;
import Fream_back.improve_Fream_Back.user.entity.ShoeSize;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class) // 테스트 설정
class UserUpdateServiceTest {

    @Autowired
    private UserUpdateService userUpdateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private User user1;

    @BeforeEach
    void setUp() {
        // 기존 비밀번호 암호화 저장
        user1.updateLoginInfo(
                null, passwordEncoder.encode("password123"), null, null, null, null, null, null
        );
        userRepository.save(user1);
    }

    @Test
    @DisplayName("이메일 변경 테스트")
    void testUpdateEmail() {
        // Given
        LoginInfoUpdateDto dto = new LoginInfoUpdateDto();
        dto.setNewEmail("newemail@example.com");

        // When
        userUpdateService.updateLoginInfo(user1.getEmail(), dto);

        // Then
        User updatedUser = userRepository.findByEmail("newemail@example.com").orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void testUpdatePassword() {
        // Given
        LoginInfoUpdateDto dto = new LoginInfoUpdateDto();
        dto.setPassword("password123");
        dto.setNewPassword("newpassword456");

        // When
        userUpdateService.updateLoginInfo(user1.getEmail(), dto);

        // Then
        User updatedUser = userRepository.findByEmail(user1.getEmail()).orElseThrow();
        assertThat(passwordEncoder.matches("newpassword456", updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트 - 현재 비밀번호 불일치")
    void testUpdatePasswordInvalidCurrentPassword() {
        // Given
        LoginInfoUpdateDto dto = new LoginInfoUpdateDto();
        dto.setPassword("wrongpassword");
        dto.setNewPassword("newpassword456");

        // When & Then
        assertThatThrownBy(() -> userUpdateService.updateLoginInfo(user1.getEmail(), dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("휴대폰 번호 변경 테스트")
    void testUpdatePhoneNumber() {
        // Given
        LoginInfoUpdateDto dto = new LoginInfoUpdateDto();
        dto.setNewPhoneNumber("010-2222-3333");

        // When
        userUpdateService.updateLoginInfo(user1.getEmail(), dto);

        // Then
        User updatedUser = userRepository.findByEmail(user1.getEmail()).orElseThrow();
        assertThat(updatedUser.getPhoneNumber()).isEqualTo("010-2222-3333");
    }

    @Test
    @DisplayName("신발 사이즈 변경 테스트")
    void testUpdateShoeSize() {
        // Given
        LoginInfoUpdateDto dto = new LoginInfoUpdateDto();
        dto.setNewShoeSize("SIZE_280");

        // When
        userUpdateService.updateLoginInfo(user1.getEmail(), dto);

        // Then
        User updatedUser = userRepository.findByEmail(user1.getEmail()).orElseThrow();
        assertThat(updatedUser.getShoeSize()).isEqualTo(ShoeSize.SIZE_280);
    }

    @Test
    @DisplayName("수신 동의 변경 테스트")
    void testUpdateConsent() {
        // Given
        LoginInfoUpdateDto dto = new LoginInfoUpdateDto();
        dto.setAdConsent(true);
        dto.setPrivacyConsent(false);
        dto.setSmsConsent(true);
        dto.setEmailConsent(false);

        // When
        userUpdateService.updateLoginInfo(user1.getEmail(), dto);

        // Then
        User updatedUser = userRepository.findByEmail(user1.getEmail()).orElseThrow();
        assertThat(updatedUser.isPhoneNotificationConsent()).isTrue();
        assertThat(updatedUser.isEmailNotificationConsent()).isFalse();
        assertThat(updatedUser.isOptionalPrivacyAgreement()).isFalse();
    }
}
