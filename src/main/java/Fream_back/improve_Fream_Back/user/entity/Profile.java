package Fream_back.improve_Fream_Back.user.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 프로필 소유 사용자

    @Column(nullable = false, unique = true)
    private String profileName; // 프로필 이름

    private String Name; // 프로필 이름

    private String bio; // 소개글
    private boolean isPublic; // 프로필 공개 여부
    private String profileImageUrl; // 프로필 이미지 URL

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>(); // 팔로워 목록

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>(); // 팔로잉 목록

    @OneToMany(mappedBy = "blockedProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockedProfile> blockedByProfiles = new ArrayList<>(); // 나를 차단한 프로필 목록

    // **편의 메서드 - 값 업데이트**
    public void updateProfile(String profileName, String bio, Boolean isPublic, String profileImageUrl) {
        if (profileName != null) {
            this.profileName = profileName;
        }
        if (bio != null) {
            this.bio = bio;
        }
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }
    // **연관관계 메서드**

    // 팔로워 추가
    public void addFollower(Follow follow) {
        this.followers.add(follow); // 팔로워 목록에 추가
        follow.addFollowing(this.user); // Follow 엔티티에 팔로잉 설정
    }

    // 팔로워 삭제
    public void removeFollower(Follow follow) {
        this.followers.remove(follow); // 팔로워 목록에서 제거
        follow.removeFollowing(); // Follow 엔티티에서 팔로잉 해제
    }

    // 팔로잉 추가
    public void addFollowing(Follow follow) {
        this.followings.add(follow); // 팔로잉 목록에 추가
        follow.addFollower(this.user); // Follow 엔티티에 팔로워 설정
    }

    // 팔로잉 삭제
    public void removeFollowing(Follow follow) {
        this.followings.remove(follow); // 팔로잉 목록에서 제거
        follow.removeFollower(); // Follow 엔티티에서 팔로워 해제
    }

    // 차단된 프로필 추가
    public void addBlockedProfile(BlockedProfile blockedProfile) {
        this.blockedByProfiles.add(blockedProfile);
        blockedProfile.assignProfile(this);
    }

    // 차단된 프로필 삭제
    public void removeBlockedProfile(BlockedProfile blockedProfile) {
        this.blockedByProfiles.remove(blockedProfile);
        blockedProfile.assignProfile(null);
    }

    // 사용자 설정
    public void addUser(User user) {
        this.user = user;
        if (user != null && user.getProfile() != this) {
            user.addProfile(this);
        }
    }
}
