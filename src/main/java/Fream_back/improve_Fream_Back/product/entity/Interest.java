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
    @JoinColumn(name = "product_id")
    private Product product; // 관심 등록된 상품

//    private LocalDateTime createdDate; // 관심 등록일

    // 편의 메서드
    public void assignUser(User user) {
        this.user = user;
    }

    public void unassignUser() {
        this.user = null;
    }

    public void assignProduct(Product product) {
        this.product = product;
    }

    public void unassignProduct() {
        this.product = null;
    }

}
