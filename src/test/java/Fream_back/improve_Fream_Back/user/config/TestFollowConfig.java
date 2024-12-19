package Fream_back.improve_Fream_Back.user.config;

import Fream_back.improve_Fream_Back.user.entity.*;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileCommandService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestFollowConfig {

    private final ProfileCommandService profileCommandService;

    public TestFollowConfig(ProfileCommandService profileCommandService) {
        this.profileCommandService = profileCommandService;
    }

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

        User savedUser = userRepository.save(user);
        profileCommandService.createDefaultProfile(savedUser); // 디폴트 프로필 생성
        return savedUser;
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

        User savedUser = userRepository.save(user);
        profileCommandService.createDefaultProfile(savedUser); // 디폴트 프로필 생성
        return savedUser;
    }

    @Bean
    public User adminUser(UserRepository userRepository) {
        User user = User.builder()
                .email("admin@example.com")
                .password("adminpassword")
                .referralCode("admin123")
                .phoneNumber("010-0000-0000")
                .shoeSize(null) // 관리자 계정에서는 신발 사이즈 불필요
                .termsAgreement(true)
                .phoneNotificationConsent(true)
                .emailNotificationConsent(true)
                .role(Role.ADMIN) // 관리자 역할
                .age(35)
                .gender(Gender.MALE)
                .build();

        User savedUser = userRepository.save(user);
        profileCommandService.createDefaultProfile(savedUser); // 디폴트 프로필 생성
        return savedUser;
    }
}
