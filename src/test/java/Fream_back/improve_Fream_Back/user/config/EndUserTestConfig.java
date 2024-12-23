package Fream_back.improve_Fream_Back.user.config;

import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.entity.ShoeSize;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import Fream_back.improve_Fream_Back.user.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
@RequiredArgsConstructor
public class EndUserTestConfig {

    private final UserCommandService userCommandService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public User user1(UserRepository userRepository) {
        User user = User.builder()
                .email("user1@example.com")
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("010-1234-5678")
                .shoeSize(ShoeSize.SIZE_270)
                .referralCode("ref123") // referralCode 초기화
                .role(Role.USER)
                .termsAgreement(true)
                .phoneNotificationConsent(true)
                .emailNotificationConsent(false)
                .build();
        return userRepository.save(user);
    }

    @Bean
    public User user2(UserRepository userRepository) {
        User user = User.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("password456"))
                .phoneNumber("010-9876-5432")
                .shoeSize(ShoeSize.SIZE_280)
                .referralCode("ref456") // referralCode 초기화
                .role(Role.USER)
                .termsAgreement(true)
                .phoneNotificationConsent(false)
                .emailNotificationConsent(true)
                .build();
        return userRepository.save(user);
    }

    @Bean
    public User adminUser(UserRepository userRepository) {
        User user = User.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("adminpassword"))
                .phoneNumber("010-0000-0000")
                .referralCode("admin123") // referralCode 초기화
                .role(Role.ADMIN) // 관리자 역할
                .termsAgreement(true)
                .phoneNotificationConsent(true)
                .emailNotificationConsent(true)
                .build();
        return userRepository.save(user);
    }
}
