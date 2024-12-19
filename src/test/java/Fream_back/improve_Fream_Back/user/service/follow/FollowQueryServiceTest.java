package Fream_back.improve_Fream_Back.user.service.follow;

import Fream_back.improve_Fream_Back.user.config.TestFollowConfig;
import Fream_back.improve_Fream_Back.user.dto.follow.FollowDto;
import Fream_back.improve_Fream_Back.user.entity.Follow;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(TestFollowConfig.class)
class FollowQueryServiceTest {

    @Autowired
    private FollowQueryService followQueryService;

    @Autowired
    private FollowCommandService followCommandService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @Autowired
    private User adminUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // user1 -> user2 팔로우
        followCommandService.createFollow(user1.getEmail(), user2.getProfile().getId());
        // user2 -> adminUser 팔로우
        followCommandService.createFollow(user2.getEmail(), adminUser.getProfile().getId());
    }

    @Test
    @DisplayName("팔로워 조회 - 성공")
    void testGetFollowersSuccess() {
        // Given
        String email = user2.getEmail(); // user2의 팔로워 조회 (user1이 팔로우한 상태)
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<FollowDto> followers = followQueryService.getFollowers(email, pageable);

        // Then
        assertThat(followers).isNotEmpty(); // 팔로워가 있어야 함
        assertThat(followers.getTotalElements()).isEqualTo(1); // user1만 user2를 팔로우
        FollowDto followerDto = followers.getContent().get(0);

        // 팔로워 정보 검증
        assertThat(followerDto.getProfileId()).isEqualTo(user1.getProfile().getId());
        assertThat(followerDto.getProfileName()).isEqualTo(user1.getProfile().getProfileName());
        assertThat(followerDto.getProfileImageUrl()).isEqualTo(user1.getProfile().getProfileImageUrl());
    }

    @Test
    @DisplayName("팔로잉 조회 - 성공")
    void testGetFollowingsSuccess() {
        // Given
        String email = user1.getEmail(); // user1이 팔로잉한 프로필 조회 (user2를 팔로잉 중)
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<FollowDto> followings = followQueryService.getFollowings(email, pageable);

        // Then
        assertThat(followings).isNotEmpty(); // 팔로잉이 있어야 함
        assertThat(followings.getTotalElements()).isEqualTo(1); // user1이 user2만 팔로우
        FollowDto followingDto = followings.getContent().get(0);

        // 팔로잉 정보 검증
        assertThat(followingDto.getProfileId()).isEqualTo(user2.getProfile().getId());
        assertThat(followingDto.getProfileName()).isEqualTo(user2.getProfile().getProfileName());
        assertThat(followingDto.getProfileImageUrl()).isEqualTo(user2.getProfile().getProfileImageUrl());
    }


    @Test
    @DisplayName("팔로워 조회 - 실패 (존재하지 않는 사용자)")
    void testGetFollowersFailUserNotFound() {
        // Given
        String invalidEmail = "nonexistent@example.com";
        PageRequest pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> followQueryService.getFollowers(invalidEmail, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자의 프로필이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("팔로잉 조회 - 실패 (존재하지 않는 사용자)")
    void testGetFollowingsFailUserNotFound() {
        // Given
        String invalidEmail = "nonexistent@example.com";
        PageRequest pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> followQueryService.getFollowings(invalidEmail, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자의 프로필이 존재하지 않습니다.");
    }
}
