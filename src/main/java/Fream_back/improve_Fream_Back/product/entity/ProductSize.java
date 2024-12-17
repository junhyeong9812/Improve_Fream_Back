package Fream_back.improve_Fream_Back.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String size; // 사이즈 (예: 250, M, L)

    @Column(nullable = false)
    private int purchasePrice; // 구매가

    @Column(nullable = false)
    private int salePrice; // 판매가

    @Column(nullable = false)
    private int quantity; // 재고 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_color_id")
    private ProductColor productColor; // ProductColor 참조

    public void assignProductColor(ProductColor productColor) {
        this.productColor = productColor;
    }

    public void update(int purchasePrice, int salePrice, int quantity) {
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.quantity = quantity;
    }
}

