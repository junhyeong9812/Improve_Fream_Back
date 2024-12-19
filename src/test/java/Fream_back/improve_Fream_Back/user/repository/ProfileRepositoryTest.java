package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestConfig.class, TestQueryDslConfig.class}) // QueryDSL Config도 가져오기
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private User user1; // TestConfig에서 제공되는 유저1

    @Test
    @DisplayName("프로필 저장 및 조회 테스트")
    void testSaveAndFindProfile() {
        // Given
        Profile profile = Profile.builder()
                .user(user1)
                .profileName("user1_profile")
                .bio("This is user1's profile.")
                .isPublic(true)
                .build();
        profileRepository.save(profile);

        // When
        Optional<Profile> foundProfile = profileRepository.findByUser_Email(user1.getEmail());

        // Then
        assertThat(foundProfile).isPresent();
        assertThat(foundProfile.get().getProfileName()).isEqualTo("user1_profile");
    }

    @Test
    @DisplayName("Profile 수정 테스트 (더티 체킹 확인)")
    @Transactional
    void testUpdateProfileWithDirtyChecking() {
        // Given
        Profile profile = Profile.builder()
                .user(user1)
                .profileName("old_profile_name")
                .bio("Old bio")
                .isPublic(true)
                .profileImageUrl("old_image_url")
                .build();
        profileRepository.save(profile);

        // When
        profile.updateProfile("new_profile_name", "New bio", false, "new_image_url");

        // Then
        Profile updatedProfile = profileRepository.findById(profile.getId()).orElseThrow();
        assertThat(updatedProfile.getProfileName()).isEqualTo("new_profile_name");
        assertThat(updatedProfile.getBio()).isEqualTo("New bio");
        assertThat(updatedProfile.isPublic()).isFalse();
        assertThat(updatedProfile.getProfileImageUrl()).isEqualTo("new_image_url");
    }
}
