package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interest extends BaseTimeEntity {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 관심 등록한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_color_id")
    private ProductColor productColor; // 관심 등록된 상품 색상

    // 연관관계 편의 메서드
    public void assignUser(User user) {
        if (this.user != null) {
            this.user.getInterests().remove(this);
        }
        this.user = user;
        user.getInterests().add(this);
    }

    public void unassignUser() {
        if (this.user != null) {
            this.user.getInterests().remove(this);
            this.user = null;
        }
    }

    public void assignProductColor(ProductColor productColor) {
        if (this.productColor != null) {
            this.productColor.getInterests().remove(this);
        }
        this.productColor = productColor;
        productColor.getInterests().add(this);
    }

    public void unassignProductColor() {
        if (this.productColor != null) {
            this.productColor.getInterests().remove(this);
            this.productColor = null;
        }
    }
}
