package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.product.entity.size.ProductSizeAndColorQuantity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory; // 상품의 상위 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory; // 상품의 하위 카테고리

    private BigDecimal initialPrice; // 출시가 (상품의 최초 가격)
    private String description; // 상품 설명
    private LocalDate releaseDate; // 출시일 추가

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductSizeAndColorQuantity> sizeAndColorQuantities = new HashSet<>(); ; // 사이즈 및 색상별 수량 관리 목록

    @OneToMany(mappedBy = "product")
    @Builder.Default
    private Set<UserProduct> userProducts = new HashSet<>(); ; // 사용자 등록 상품 목록

    public Product(String name, String brand, String sku, BigDecimal initialPrice, String description) {
        this.name = name;
        this.brand = brand;
        this.sku = sku;
        this.initialPrice = initialPrice;
        this.description = description;
    }

    @PostPersist
    public void assignSku() {
        // SKU가 아직 없는 경우 ID를 기반으로 생성
        if (this.sku == null) {
            this.sku = "SKU-" + this.id; // SKU 형식: "SKU-<ID>"
        }
    }

    // 연관관계 편의 메서드 - UserProduct 추가
    public void addUserProduct(UserProduct userProduct) {
        this.userProducts.add(userProduct);
        userProduct.assignProduct(this);
    }
    // 연관관계 편의 메서드 - ProductSizeAndColorQuantity 추가
    public void addSizeAndColorQuantity(ProductSizeAndColorQuantity sizeAndColorQuantity) {
        this.sizeAndColorQuantities.add(sizeAndColorQuantity);
        sizeAndColorQuantity.assignProduct(this);
    }
    // 상품 정보 수정 메서드
    public void updateProductInfo(String name, String brand, String sku, MainCategory mainCategory, SubCategory subCategory, BigDecimal initialPrice, String description,LocalDate releaseDate) {
        this.name = name;
        this.brand = brand;
        this.sku = sku;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.initialPrice = initialPrice;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) &&
                Objects.equals(brand, product.brand) &&
                Objects.equals(sku, product.sku) &&
                Objects.equals(initialPrice, product.initialPrice) &&
                Objects.equals(description, product.description) &&
                Objects.equals(releaseDate, product.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, brand, sku, initialPrice, description, releaseDate);
    }



}
