package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import Fream_back.improve_Fream_Back.user.dto.UserRegistrationDto;
import Fream_back.improve_Fream_Back.user.entity.ShoeSize;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileCommandService;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
@Import(TestConfig.class) // TestConfig를 사용하여 user1, user2 생성
class UserCommandServiceTest {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileCommandService profileService; // 실제 빈 사용

    @Autowired
    private FileUtils fileUtils; // 실제 파일 관련 로직 테스트

    @Autowired
    private PasswordEncoder passwordEncoder; // 실제 비밀번호 암호화 사용

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @Test
    @DisplayName("회원가입 테스트 - 성공 시나리오")
    void testRegisterUser() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("newuser@example.com");
        dto.setPassword("password123");
        dto.setPhoneNumber("01012345678"); // phoneNumber 설정
        dto.setIsOver14(true);
        dto.setTermsAgreement(true);
        dto.setPrivacyAgreement(true);
        dto.setShoeSize(ShoeSize.SIZE_270);

        // When
        User registeredUser = userCommandService.registerUser(dto);

        // Then
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(registeredUser.getPassword()).isNotEqualTo("password123"); // 암호화 확인
        assertThat(passwordEncoder.matches("password123", registeredUser.getPassword())).isTrue();
        assertThat(registeredUser.getShoeSize()).isEqualTo(ShoeSize.SIZE_270);
    }

    @Test
    @DisplayName("회원가입 테스트 - 추천인 코드 없는 경우")
    void testRegisterUserWithoutReferralCode() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("newuser@example.com");
        dto.setPassword("password123");
        dto.setPhoneNumber("01012345678"); // phoneNumber 설정
        dto.setIsOver14(true);
        dto.setTermsAgreement(true);
        dto.setPrivacyAgreement(true);

        // When
        User registeredUser = userCommandService.registerUser(dto);

        // Then
        assertThat(registeredUser.getReferrer()).isNull();
    }

    @Test
    @DisplayName("회원가입 테스트 - 추천인 코드 있는 경우")
    void testRegisterUserWithReferralCode() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("newuser@example.com");
        dto.setPassword("password123");
        dto.setPhoneNumber("01012345678");
        dto.setIsOver14(true);
        dto.setTermsAgreement(true);
        dto.setPrivacyAgreement(true);
        dto.setReferralCode(user1.getReferralCode()); // user1의 추천인 코드 사용

        // When
        User registeredUser = userCommandService.registerUser(dto);

        // Then
        assertThat(registeredUser.getReferrer()).isNotNull();
        assertThat(registeredUser.getReferrer().getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    @DisplayName("회원 탈퇴 테스트 - 정상 삭제")
    void testDeleteAccount() {
        // Given
        userRepository.save(user1);

        // When
        userCommandService.deleteAccount(user1.getEmail());

        // Then
        assertThat(userRepository.findByEmail(user1.getEmail())).isEmpty();
    }

    @Test
    @DisplayName("회원 탈퇴 테스트 - 없는 사용자")
    void testDeleteAccountWithNonExistingUser() {
        // Given
        String nonExistingEmail = "notexist@example.com";

        // When & Then
        assertThatThrownBy(() -> userCommandService.deleteAccount(nonExistingEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자를 찾을 수 없습니다.");
    }
}
