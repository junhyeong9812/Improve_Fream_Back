package Fream_back.improve_Fream_Back.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 프로필 소유 사용자

    private String profileName; // 프로필 이름
    private String bio; // 소개 글
    private boolean profilePublic; // 공개 여부
    private String profileImageUrl; // 프로필 이미지 URL

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockedUser> blockedUsers = new ArrayList<>(); // 차단된 사용자 목록

    // 연관관계 메서드
    public void blockUser(BlockedUser blockedUser) {
        this.blockedUsers.add(blockedUser);
        blockedUser.assignProfile(this);
    }

    public void unblockUser(BlockedUser blockedUser) {
        this.blockedUsers.remove(blockedUser);
    }

    // 프로필 이미지 URL 업데이트 메서드
    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
