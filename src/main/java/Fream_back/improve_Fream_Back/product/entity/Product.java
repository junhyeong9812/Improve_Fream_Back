package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product
 *
 * 기본 상품 정보를 관리하는 엔티티입니다.
 * 상품명, 브랜드, 출시가 등 기본적인 정보를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID (기본 키)

    private String name; // 상품명
    private String brand; // 브랜드명
    private String sku; // SKU (고유 제품 코드)

    private BigDecimal initialPrice; // 출시가 (상품의 최초 가격)
    private String description; // 상품 설명

    private int registeredCount; // 등록된 사용자 상품 수량

    @OneToMany(mappedBy = "product")
    private List<UserProduct> userProducts; // 사용자 등록 상품 목록

    public Product(String name, String brand, String sku, BigDecimal initialPrice, String description) {
        this.name = name;
        this.brand = brand;
        this.sku = sku;
        this.initialPrice = initialPrice;
        this.description = description;
    }

    // 연관관계 편의 메서드 - UserProduct 추가
    public void addUserProduct(UserProduct userProduct) {
        this.userProducts.add(userProduct);
        userProduct.assignProduct(this);
    }


}
