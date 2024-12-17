package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int price; // 변동 가격

    @Column(nullable = false)
    private String priceType; // 구매가 or 판매가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_size_id")
    private ProductSize productSize;

    public void assignProductSize(ProductSize productSize) {
        this.productSize = productSize;
    }
}
