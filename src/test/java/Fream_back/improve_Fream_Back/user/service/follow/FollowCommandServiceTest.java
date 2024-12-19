package Fream_back.improve_Fream_Back.user.service.follow;

import Fream_back.improve_Fream_Back.notification.service.NotificationCommandService;
import Fream_back.improve_Fream_Back.user.config.TestFollowConfig;
import Fream_back.improve_Fream_Back.user.entity.Follow;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.FollowRepository;
import Fream_back.improve_Fream_Back.user.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
class FollowCommandServiceTest {

    @Autowired
    private FollowCommandService followCommandService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @Mock
    private NotificationCommandService notificationCommandService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("팔로우 생성 - 성공")
    void testCreateFollowSuccess() {
        // Given
        String followerEmail = user1.getEmail();
        Long followingProfileId = user2.getProfile().getId();

        // When
        followCommandService.createFollow(followerEmail, followingProfileId);

        // Then
        Follow follow = followRepository.findByFollowerAndFollowing(
                user1.getProfile(), user2.getProfile()
        ).orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 생성되지 않았습니다."));

        assertThat(follow).isNotNull();
        assertThat(follow.getFollower().getId()).isEqualTo(user1.getProfile().getId());
        assertThat(follow.getFollowing().getId()).isEqualTo(user2.getProfile().getId());
    }


    @Test
    @DisplayName("팔로우 생성 - 실패 (팔로우 대상 프로필이 존재하지 않을 경우)")
    void testCreateFollowFailProfileNotFound() {
        // Given
        String followerEmail = user1.getEmail();
        Long invalidProfileId = 999L;

        // When & Then
        assertThatThrownBy(() -> followCommandService.createFollow(followerEmail, invalidProfileId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("팔로우 대상 프로필이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("팔로우 삭제 - 성공")
    void testDeleteFollowSuccess() {
        // Given
        String followerEmail = user1.getEmail();
        Long followingProfileId = user2.getProfile().getId();

        followCommandService.createFollow(followerEmail, followingProfileId);

        // When
        followCommandService.deleteFollow(followerEmail, followingProfileId);

        // Then
        assertThatThrownBy(() -> followRepository.findByFollowerAndFollowing(
                user1.getProfile(), user2.getProfile()
        ).orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 삭제되지 않았습니다.")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("팔로우 관계가 삭제되지 않았습니다.");
    }

    @Test
    @DisplayName("팔로우 삭제 - 실패 (존재하지 않는 팔로우 관계)")
    void testDeleteFollowFailNoFollowRelation() {
        // Given
        String followerEmail = user1.getEmail();
        Long followingProfileId = user2.getProfile().getId();

        // When & Then
        assertThatThrownBy(() -> followCommandService.deleteFollow(followerEmail, followingProfileId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("팔로우 관계가 존재하지 않습니다.");
    }
}