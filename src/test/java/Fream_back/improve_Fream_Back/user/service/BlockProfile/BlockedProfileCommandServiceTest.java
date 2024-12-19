package Fream_back.improve_Fream_Back.user.service.BlockProfile;

import Fream_back.improve_Fream_Back.user.config.TestFollowConfig;
import Fream_back.improve_Fream_Back.user.entity.BlockedProfile;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.BlockedProfileRepository;
import Fream_back.improve_Fream_Back.user.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(TestFollowConfig.class)
class BlockedProfileCommandServiceTest {

    @Autowired
    private BlockedProfileCommandService blockedProfileCommandService;

    @Autowired
    private BlockedProfileRepository blockedProfileRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @Autowired
    private User adminUser;


    @Test
    @DisplayName("프로필 차단 - 성공")
    void testBlockProfileSuccess() {
        // Given
        String email = user1.getEmail();
        Long blockedProfileId = user2.getProfile().getId();

        // When
        blockedProfileCommandService.blockProfile(email, blockedProfileId);

        // Then
        BlockedProfile blockedProfile = blockedProfileRepository.findByProfileAndBlockedProfile(
                user1.getProfile(), user2.getProfile()
        ).orElseThrow(() -> new IllegalArgumentException("차단된 프로필이 생성되지 않았습니다."));

        assertThat(blockedProfile).isNotNull();
        assertThat(blockedProfile.getProfile().getId()).isEqualTo(user1.getProfile().getId());
        assertThat(blockedProfile.getBlockedProfile().getId()).isEqualTo(user2.getProfile().getId());
    }

    @Test
    @DisplayName("프로필 차단 - 실패 (이미 차단된 프로필)")
    void testBlockProfileFailAlreadyBlocked() {
        // Given
        String email = user1.getEmail();
        Long blockedProfileId = user2.getProfile().getId();

        blockedProfileCommandService.blockProfile(email, blockedProfileId);

        // When & Then
        assertThatThrownBy(() -> blockedProfileCommandService.blockProfile(email, blockedProfileId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 차단된 프로필입니다.");
    }

    @Test
    @DisplayName("프로필 차단 해제 - 성공")
    void testUnblockProfileSuccess() {
        // Given
        String email = user1.getEmail();
        Long blockedProfileId = user2.getProfile().getId();

        blockedProfileCommandService.blockProfile(email, blockedProfileId);

        // When
        blockedProfileCommandService.unblockProfile(email, blockedProfileId);

        // Then
        assertThat(blockedProfileRepository.findByProfileAndBlockedProfile(
                user1.getProfile(), user2.getProfile()
        )).isEmpty();
    }

    @Test
    @DisplayName("프로필 차단 해제 - 실패 (차단 목록에 없는 프로필)")
    void testUnblockProfileFailNotBlocked() {
        // Given
        String email = user1.getEmail();
        Long blockedProfileId = user2.getProfile().getId();

        // When & Then
        assertThatThrownBy(() -> blockedProfileCommandService.unblockProfile(email, blockedProfileId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 프로필은 차단 목록에 없습니다.");
    }

    @Test
    @DisplayName("프로필 차단 - 실패 (차단할 프로필이 존재하지 않음)")
    void testBlockProfileFailProfileNotFound() {
        // Given
        String email = user1.getEmail();
        Long invalidProfileId = 999L;

        // When & Then
        assertThatThrownBy(() -> blockedProfileCommandService.blockProfile(email, invalidProfileId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차단할 프로필을 찾을 수 없습니다.");
    }
}
