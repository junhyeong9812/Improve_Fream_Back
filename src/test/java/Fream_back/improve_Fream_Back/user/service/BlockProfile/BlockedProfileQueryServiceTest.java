package Fream_back.improve_Fream_Back.user.service.BlockProfile;

import Fream_back.improve_Fream_Back.user.config.TestFollowConfig;
import Fream_back.improve_Fream_Back.user.dto.BlockedProfileDto;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Import(TestFollowConfig.class)
class BlockedProfileQueryServiceTest {

    @Autowired
    private BlockedProfileCommandService blockedProfileCommandService;

    @Autowired
    private BlockedProfileQueryService blockedProfileQueryService;

    @Autowired
    private ProfileQueryService profileQueryService;

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @BeforeEach
    void setup() {
        // user1이 user2를 차단
        blockedProfileCommandService.blockProfile(user1.getEmail(), user2.getProfile().getId());
    }

    @Test
    @DisplayName("차단된 프로필 조회 - 성공 (프로필 기준)")
    void testGetBlockedProfilesByProfile() {
        // Given
        Profile profile = profileQueryService.getProfileByEmail(user1.getEmail());

        // When
        List<BlockedProfileDto> blockedProfiles = blockedProfileQueryService.getBlockedProfiles(profile);

        // Then
        assertThat(blockedProfiles).isNotEmpty();
        assertThat(blockedProfiles).hasSize(1);
        BlockedProfileDto blockedProfileDto = blockedProfiles.get(0);
        assertThat(blockedProfileDto.getProfileId()).isEqualTo(user2.getProfile().getId());
        assertThat(blockedProfileDto.getProfileName()).isEqualTo(user2.getProfile().getProfileName());
        assertThat(blockedProfileDto.getProfileImageUrl()).isEqualTo(user2.getProfile().getProfileImageUrl());
    }

    @Test
    @DisplayName("차단된 프로필 조회 - 성공 (이메일 기준)")
    void testGetBlockedProfilesByEmail() {
        // When
        List<BlockedProfileDto> blockedProfiles = blockedProfileQueryService.getBlockedProfiles(user1.getEmail());

        // Then
        assertThat(blockedProfiles).isNotEmpty();
        assertThat(blockedProfiles).hasSize(1);
        BlockedProfileDto blockedProfileDto = blockedProfiles.get(0);
        assertThat(blockedProfileDto.getProfileId()).isEqualTo(user2.getProfile().getId());
        assertThat(blockedProfileDto.getProfileName()).isEqualTo(user2.getProfile().getProfileName());
        assertThat(blockedProfileDto.getProfileImageUrl()).isEqualTo(user2.getProfile().getProfileImageUrl());
    }
}
