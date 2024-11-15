package Fream_back.improve_Fream_Back.product.entity.size;

import Fream_back.improve_Fream_Back.base.entity.BaseEntity;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.enumType.ClothingSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.Color;
import Fream_back.improve_Fream_Back.product.entity.enumType.ShoeSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSizeAndColorQuantity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 해당 상품

    @Enumerated(EnumType.STRING)
    private SizeType sizeType; // 사이즈 타입 (의류인지 신발인지 구분)

    @Enumerated(EnumType.STRING)
    private ClothingSizeType clothingSize; // 의류 사이즈 (의류일 경우 사용)

    @Enumerated(EnumType.STRING)
    private Color color; // 색상 (공통적으로 사용)

    @Enumerated(EnumType.STRING)
    private ShoeSizeType shoeSize; // 신발 사이즈 (신발일 경우 사용)

    private int quantity; // 해당 사이즈의 상품 수량

    // 연관관계 편의 메서드
    public void assignProduct(Product product) {
        this.product = product;
    }
    // 수량 업데이트 메서드
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
