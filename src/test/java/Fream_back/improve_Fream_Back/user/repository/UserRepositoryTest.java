package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User 저장 및 조회 테스트")
    public void saveAndFindUser() {
        // User 엔티티 생성 및 저장
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
        userRepository.save(user);

        // User 조회
        Optional<User> foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getPassword()).isEqualTo("password");
        assertThat(foundUser.get().getNickname()).isEqualTo("testnickname");
        assertThat(foundUser.get().getRealName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getPhoneNumber()).isEqualTo("123-4567-8901");
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser@example.com");
        assertThat(foundUser.get().getPhoneNotificationConsent()).isTrue();
        assertThat(foundUser.get().getEmailNotificationConsent()).isFalse();
        assertThat(foundUser.get().getRole()).isEqualTo(Role.USER);
    }
}
