package Fream_back.improve_Fream_Back.user.service.follow;

import Fream_back.improve_Fream_Back.user.entity.Follow;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.repository.FollowRepository;
import Fream_back.improve_Fream_Back.user.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FollowCommandService {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void createFollow(String email, Long followingProfileId) {
        // 현재 사용자의 프로필 가져오기
        Profile followerProfile = profileRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자의 프로필이 존재하지 않습니다."));

        // 팔로우 대상 프로필 가져오기
        Profile followingProfile = profileRepository.findById(followingProfileId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 대상 프로필이 존재하지 않습니다."));

        // 팔로우 관계 생성
        Follow follow = Follow.builder()
                .follower(followerProfile)
                .following(followingProfile)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void deleteFollow(String email, Long followingProfileId) {
        // 팔로우를 한 사용자 (follower) 프로필 조회
        Profile followerProfile = profileRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자의 프로필이 존재하지 않습니다."));

        // 팔로우된 사용자 (following) 프로필 조회
        Profile followingProfile = profileRepository.findById(followingProfileId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 대상 프로필이 존재하지 않습니다."));

        // 팔로우 관계 조회
        Follow follow = followRepository.findByFollowerAndFollowing(followerProfile, followingProfile)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 관계가 존재하지 않습니다."));

        // 팔로우 관계 삭제
        followRepository.delete(follow);
    }
}