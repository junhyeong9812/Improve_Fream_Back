package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // 상세페이지 이미지 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_color_id", nullable = false)
    private ProductColor productColor; // 연관된 상품 색상

    // 연관관계 편의 메서드
    public void assignProductColor(ProductColor productColor) {
        this.productColor = productColor;
    }
}
