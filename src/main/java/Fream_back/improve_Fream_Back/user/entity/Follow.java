package Fream_back.improve_Fream_Back.user.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "follows")
public class Follow extends BaseTimeEntity { // BaseTimeEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User follower; // 팔로우를 한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private User following; // 팔로우된 사용자

    // **연관관계 메서드**

    // 팔로워 설정
    public void addFollower(User follower) {
        this.follower = follower; // 팔로워 설정
        if (!follower.getProfile().getFollowings().contains(this)) {
            follower.getProfile().addFollowing(this); // 팔로워의 팔로잉 목록에 추가
        }
    }

    // 팔로워 해제
    public void removeFollower() {
        if (this.follower != null) {
            this.follower.getProfile().getFollowings().remove(this); // 팔로워의 팔로잉 목록에서 제거
        }
        this.follower = null; // 팔로워 해제
    }

    // 팔로잉 설정
    public void addFollowing(User following) {
        this.following = following; // 팔로잉 설정
        if (!following.getProfile().getFollowers().contains(this)) {
            following.getProfile().addFollower(this); // 팔로잉의 팔로워 목록에 추가
        }
    }

    // 팔로잉 해제
    public void removeFollowing() {
        if (this.following != null) {
            this.following.getProfile().getFollowers().remove(this); // 팔로잉의 팔로워 목록에서 제거
        }
        this.following = null; // 팔로잉 해제
    }
}