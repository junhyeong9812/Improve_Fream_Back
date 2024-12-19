package Fream_back.improve_Fream_Back.user.config;

import Fream_back.improve_Fream_Back.user.entity.*;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public User user1(UserRepository userRepository) {
        User user = User.builder()
                .email("user1@example.com")
                .password("password123")
                .referralCode("ref123")
                .phoneNumber("010-1234-5678")
                .shoeSize(ShoeSize.SIZE_270)
                .termsAgreement(true)
                .phoneNotificationConsent(false)
                .emailNotificationConsent(true)
                .role(Role.USER)
                .age(25)
                .gender(Gender.MALE)
                .build();

        return userRepository.save(user);
    }

    @Bean
    public User user2(UserRepository userRepository) {
        User user = User.builder()
                .email("user2@example.com")
                .password("password456")
                .referralCode("ref456")
                .phoneNumber("010-9876-5432")
                .shoeSize(ShoeSize.SIZE_280)
                .termsAgreement(true)
                .phoneNotificationConsent(true)
                .emailNotificationConsent(false)
                .role(Role.USER)
                .age(30)
                .gender(Gender.FEMALE)
                .build();

        return userRepository.save(user);
    }
}
