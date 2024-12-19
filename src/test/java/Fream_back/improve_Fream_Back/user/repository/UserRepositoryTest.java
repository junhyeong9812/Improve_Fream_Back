package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig; // QueryDSL Config 추가
import Fream_back.improve_Fream_Back.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestConfig.class, TestQueryDslConfig.class}) // QueryDSL Config도 가져오기
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private User user1; // TestConfig에서 제공되는 유저1
    @Autowired
    private User user2; // TestConfig에서 제공되는 유저2

    @Test
    @DisplayName("유저 저장 및 조회 테스트")
    void testSaveAndFindUser() {
        // Given: TestConfig에서 이미 유저가 저장됨
        Optional<User> foundUser = userRepository.findById(user1.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    @DisplayName("추천인 코드로 유저 조회")
    void testFindByReferralCode() {
        // When
        Optional<User> foundUser = userRepository.findByReferralCode("ref123");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    @DisplayName("이메일로 유저 조회")
    void testFindByEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("user2@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getPassword()).isEqualTo("password456");
    }

    @Test
    @DisplayName("휴대폰 번호로 유저 조회")
    void testFindByPhoneNumber() {
        // When
        Optional<User> foundUser = userRepository.findByPhoneNumber("010-1234-5678");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getReferralCode()).isEqualTo("ref123");
    }

    @Test
    @DisplayName("이메일과 휴대폰 번호로 유저 조회")
    void testFindByEmailAndPhoneNumber() {
        // When
        Optional<User> foundUser = userRepository.findByEmailAndPhoneNumber("user2@example.com", "010-9876-5432");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRole()).isEqualTo(user2.getRole());
    }
}
