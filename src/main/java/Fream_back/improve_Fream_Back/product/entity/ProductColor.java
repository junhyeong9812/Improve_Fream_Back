package Fream_back.improve_Fream_Back.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 색상 ID

    @Column(nullable = false)
    private String colorName; // 색상명 (예: Midnight Navy)

    @Lob
    private String content; // 상세페이지 (HTML)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 상위 Product 참조

    @OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images; // 색상별 이미지

    @OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> sizes; // 사이즈 정보

    @OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetailImage> productDetailImages = new ArrayList<>();

    @OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();

    // 연관관계 편의 메서드
    public void assignProduct(Product product) {
        this.product = product;
    }

    public void addProductImage(ProductImage image) {
        productImages.add(image);
        image.assignProductColor(this);
    }

    public void addProductDetailImage(ProductDetailImage detailImage) {
        productDetailImages.add(detailImage);
        detailImage.assignProductColor(this);
    }

    public void addProductSize(ProductSize size) {
        sizes.add(size);
        size.assignProductColor(this);
    }

    public void update(String colorName, String content) {
        this.colorName = colorName;
        this.content = content;
    }


}

