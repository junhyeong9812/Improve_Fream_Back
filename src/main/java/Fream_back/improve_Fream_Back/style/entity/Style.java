package Fream_back.improve_Fream_Back.style.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Style extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 스타일 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem; // 연관된 주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 스타일 작성자 (유저)

    private String content; // 스타일 내용 (예: 코디 설명)
    private Integer rating; // 별점 (옵션)

    private String imageUrl; // 업로드된 사진 URL
    private String videoUrl; // 업로드된 동영상 URL

    // 연관관계 편의 메서드
    public void assignOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public void assignUser(User user) {
        this.user = user;
    }

    // 스타일 업데이트 메서드
    public void updateStyle(String content, Integer rating, String imageUrl, String videoUrl) {
        this.content = content;
        this.rating = rating;

        // 유효성 검증: 이미지와 비디오 중 하나만 허용
        if (imageUrl != null && videoUrl != null) {
            throw new IllegalArgumentException("이미지와 비디오는 동시에 업로드할 수 없습니다.");
        }
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }
}
