package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import Fream_back.improve_Fream_Back.user.entity.Follow;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestConfig.class, TestQueryDslConfig.class}) // QueryDSL Config도 가져오기
class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private User user1; // TestConfig에서 제공되는 유저1
    @Autowired
    private User user2; // TestConfig에서 제공되는 유저2

    @Test
    @DisplayName("팔로우 저장 및 조회 테스트")
    void testSaveAndFindFollow() {
        // Given
        Profile follower = Profile.builder().user(user1).profileName("follower").build();
        Profile following = Profile.builder().user(user2).profileName("following").build();
        profileRepository.save(follower);
        profileRepository.save(following);

        Follow follow = Follow.builder().follower(follower).following(following).build();
        followRepository.save(follow);

        // When
        Optional<Follow> foundFollow = followRepository.findByFollowerAndFollowing(follower, following);

        // Then
        assertThat(foundFollow).isPresent();
    }

    @Test
    @DisplayName("팔로워 목록 조회 테스트")
    void testFindFollowersByProfileId() {
        // Given
        Profile follower = Profile.builder().user(user1).profileName("follower").build();
        Profile following = Profile.builder().user(user2).profileName("following").build();
        profileRepository.save(follower);
        profileRepository.save(following);
        System.out.println("following.getId() = " + following.getId());
        System.out.println("follower = " + follower.getId());

        Follow follow = Follow.builder().follower(follower).following(following).build();
        followRepository.save(follow);


        // When
        Page<Follow> followers = followRepository.findFollowersByProfileId(follower.getId(), PageRequest.of(0, 10));        System.out.println("followers = " + followers);
        // Then
        assertThat(followers).isNotEmpty();
    }
}
