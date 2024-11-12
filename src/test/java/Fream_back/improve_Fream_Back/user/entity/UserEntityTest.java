package Fream_back.improve_Fream_Back.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserEntityTest {

    @Test
    @DisplayName("User 엔티티 생성 테스트")
    public void createUser() {
        // User 엔티티 생성
        User user = User.builder()
                .username("testuser")
                .password("password")
                .nickname("testnickname")
                .realName("John Doe")
                .phoneNumber("123-4567-8901")
                .email("testuser@example.com")
                .phoneNotificationConsent(true)
                .emailNotificationConsent(false)
                .role(Role.USER)
                .build();

        // 데이터 검증
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getNickname()).isEqualTo("testnickname");
        assertThat(user.getRealName()).isEqualTo("John Doe");
        assertThat(user.getPhoneNumber()).isEqualTo("123-4567-8901");
        assertThat(user.getEmail()).isEqualTo("testuser@example.com");
        assertThat(user.getPhoneNotificationConsent()).isTrue();
        assertThat(user.getEmailNotificationConsent()).isFalse();
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }
}
