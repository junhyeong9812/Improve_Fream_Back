package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.dto.EmailFindRequestDto;
import Fream_back.improve_Fream_Back.user.dto.LoginInfoDto;
import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class)
class UserQueryServiceTest {

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @Autowired
    private User adminUser;

    @Test
    @DisplayName("관리자 권한 확인 테스트 - 성공")
    void testCheckAdminRoleSuccess() {
        // When & Then
        userQueryService.checkAdminRole(adminUser.getEmail());
    }

    @Test
    @DisplayName("관리자 권한 확인 테스트 - 실패")
    void testCheckAdminRoleFailure() {
        // When & Then
        assertThatThrownBy(() -> userQueryService.checkAdminRole(user1.getEmail()))
                .isInstanceOf(SecurityException.class)
                .hasMessage("관리자 권한이 없습니다.");
    }

    @Test
    @DisplayName("휴대전화 번호로 이메일 정보 조회 - 성공")
    void testFindEmailByPhoneNumberSuccess() {
        // Given
        EmailFindRequestDto requestDto = new EmailFindRequestDto(user1.getPhoneNumber());

        // When
        String email = userQueryService.findEmailByPhoneNumber(requestDto);

        // Then
        assertThat(email).isEqualTo(user1.getEmail());
    }

    @Test
    @DisplayName("휴대전화 번호로 이메일 정보 조회 - 실패")
    void testFindEmailByPhoneNumberFailure() {
        // Given
        EmailFindRequestDto requestDto = new EmailFindRequestDto("010-9876-5431");

        // When & Then
        assertThatThrownBy(() -> userQueryService.findEmailByPhoneNumber(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 휴대폰 번호로 등록된 사용자가 없습니다.");
    }

    @Test
    @DisplayName("로그인 정보 조회 - 성공")
    void testGetLoginInfoSuccess() {
        // When
        LoginInfoDto loginInfo = userQueryService.getLoginInfo(user1.getEmail());

        // Then
        assertThat(loginInfo).isNotNull();
        assertThat(loginInfo.getEmail()).isEqualTo(user1.getEmail());
        assertThat(loginInfo.getPhoneNumber()).isEqualTo(user1.getPhoneNumber());
        assertThat(loginInfo.getShoeSize()).isEqualTo(user1.getShoeSize().name());
        assertThat(loginInfo.getOptionalPrivacyAgreement()).isEqualTo(user1.isOptionalPrivacyAgreement());
        assertThat(loginInfo.getSmsConsent()).isEqualTo(user1.isPhoneNotificationConsent());
        assertThat(loginInfo.getEmailConsent()).isEqualTo(user1.isEmailNotificationConsent());
    }

    @Test
    @DisplayName("로그인 정보 조회 - 실패")
    void testGetLoginInfoFailure() {
        // When & Then
        assertThatThrownBy(() -> userQueryService.getLoginInfo("notexist@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 사용자를 찾을 수 없습니다.");
    }
}
