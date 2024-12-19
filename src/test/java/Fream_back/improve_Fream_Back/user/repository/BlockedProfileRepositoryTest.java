package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import Fream_back.improve_Fream_Back.user.entity.BlockedProfile;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestConfig.class, TestQueryDslConfig.class}) // QueryDSL Config도 가져오기
class BlockedProfileRepositoryTest {

    @Autowired
    private BlockedProfileRepository blockedProfileRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private User user1; // TestConfig에서 제공되는 유저1
    @Autowired
    private User user2; // TestConfig에서 제공되는 유저2

    @Test
    @DisplayName("차단된 프로필 저장 및 조회 테스트")
    void testSaveAndFindBlockedProfile() {
        // Given
        Profile blocker = Profile.builder().user(user1).profileName("blocker").build();
        Profile blocked = Profile.builder().user(user2).profileName("blocked").build();
        profileRepository.save(blocker);
        profileRepository.save(blocked);

        BlockedProfile blockedProfile = BlockedProfile.builder().profile(blocker).blockedProfile(blocked).build();
        blockedProfileRepository.save(blockedProfile);

        // When
        List<BlockedProfile> blockedProfiles = blockedProfileRepository.findByProfile(blocker);

        // Then
        assertThat(blockedProfiles).hasSize(1);
        assertThat(blockedProfiles.get(0).getBlockedProfile().getProfileName()).isEqualTo("blocked");
    }
}
