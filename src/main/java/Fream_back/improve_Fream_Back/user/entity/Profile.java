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
}
