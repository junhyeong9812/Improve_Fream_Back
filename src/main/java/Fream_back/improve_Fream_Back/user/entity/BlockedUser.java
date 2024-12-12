package Fream_back.improve_Fream_Back.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile; // 차단을 설정한 프로필

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_user_id")
    private User blockedUser; // 차단된 사용자

    // 연관관계 메서드
    public void assignProfile(Profile profile) {
        this.profile = profile;
    }

    public void assignBlockedUser(User user) {
        this.blockedUser = user;
    }
}
