package Fream_back.improve_Fream_Back.user.service.profile;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.dto.BlockedProfileDto;
import Fream_back.improve_Fream_Back.user.dto.ProfileInfoDto;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.BlockProfile.BlockedProfileQueryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(TestConfig.class)
class ProfileQueryServiceTest {

    @Autowired
    private ProfileCommandService profileCommandService;

    @Autowired
    private ProfileQueryService profileQueryService;

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @Autowired
    private User adminUser;



    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        profileCommandService.createDefaultProfile(user1);
        profileCommandService.createDefaultProfile(user2);
        profileCommandService.createDefaultProfile(adminUser);
    }

    @Test
    @DisplayName("프로필 정보 조회 - 성공")
    void testGetProfileInfo() {
        // Given
        String email = user1.getEmail();


        // When
        ProfileInfoDto profileInfo = profileQueryService.getProfileInfo(email);

        // Then
        assertThat(profileInfo.getProfileImage()).isEqualTo("user.jpg");
        assertThat(profileInfo.getProfileName()).isNotEmpty();
        assertThat(profileInfo.getRealName()).isEqualTo("user1");
        assertThat(profileInfo.getBio()).isEmpty();
        assertThat(profileInfo.getIsPublic()).isTrue();
        assertThat(profileInfo.getBlockedProfiles()).isEmpty();
    }

    @Test
    @DisplayName("프로필 정보 조회 - 실패 (존재하지 않는 이메일)")
    void testGetProfileInfoNotFound() {
        // Given
        String email = "nonexistent@example.com";

        // When & Then
        assertThatThrownBy(() -> profileQueryService.getProfileInfo(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로필을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("프로필 이미지 파일명 조회 - 성공")
    void testGetProfileImageFileName() {
        // Given
        Long profileId = user1.getProfile().getId();

        // When
        String fileName = profileQueryService.getProfileImageFileName(profileId);

        // Then
        assertThat(fileName).isEqualTo("user.jpg");
    }

    @Test
    @DisplayName("프로필 이미지 파일명 조회 - 실패 (존재하지 않는 프로필 ID)")
    void testGetProfileImageFileNameNotFound() {
        // Given
        Long nonExistentProfileId = 999L;

        // When & Then
        assertThatThrownBy(() -> profileQueryService.getProfileImageFileName(nonExistentProfileId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로필을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("이메일로 프로필 조회 - 성공")
    void testGetProfileByEmail() {
        // Given
        String email = user2.getEmail();

        // When
        Profile profile = profileQueryService.getProfileByEmail(email);

        // Then
        assertThat(profile).isNotNull();
        assertThat(profile.getUser().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일로 프로필 조회 - 실패 (존재하지 않는 이메일)")
    void testGetProfileByEmailNotFound() {
        // Given
        String email = "nonexistent@example.com";

        // When & Then
        assertThatThrownBy(() -> profileQueryService.getProfileByEmail(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로필을 찾을 수 없습니다.");
    }
}
