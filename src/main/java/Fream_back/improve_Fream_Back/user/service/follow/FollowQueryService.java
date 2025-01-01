package Fream_back.improve_Fream_Back.user.service.follow;
import Fream_back.improve_Fream_Back.user.dto.follow.FollowDto;
import Fream_back.improve_Fream_Back.user.entity.Follow;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.repository.FollowRepository;
import Fream_back.improve_Fream_Back.user.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowQueryService {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;

    public Page<FollowDto> getFollowers(String email, Pageable pageable) {
        Profile profile = profileRepository.findByUserEmailWithFetchJoin(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자의 프로필이 존재하지 않습니다."));

        Page<Follow> followers = followRepository.findFollowersByProfileId(profile.getId(), pageable);
        return followers.map(follow -> new FollowDto(
                follow.getFollower().getId(),
                follow.getFollower().getProfileName(),
                follow.getFollower().getProfileImageUrl()
        ));
    }

    public Page<FollowDto> getFollowings(String email, Pageable pageable) {
        Profile profile = profileRepository.findByUserEmailWithFetchJoin(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자의 프로필이 존재하지 않습니다."));

        Page<Follow> followings = followRepository.findFollowingsByProfileId(profile.getId(), pageable);
        return followings.map(follow -> new FollowDto(
                follow.getFollowing().getId(),
                follow.getFollowing().getProfileName(),
                follow.getFollowing().getProfileImageUrl()
        ));
    }
}