package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID

    @Column(nullable = false)
    private String name; // 상품명

    @Column(nullable = false)
    private String englishName; // 상품 영어명

    @Column(nullable = false)
    private int releasePrice; // 발매가

    @Column(nullable = false)
    private String modelNumber; // 모델 번호

    @Column(nullable = false)
    private String releaseDate; // 출시일 (YYYY-MM-DD)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand; // 브랜드 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // 상위 카테고리 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection; // 컬렉션 참조

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductColor> colors; // 색상별 상세 정보

    // 연관관계 편의 메서드
    // 연관관계 편의 메서드
    public void addProductColor(ProductColor color) {
        colors.add(color);
        color.assignProduct(this);
    }

    public void update(String name, String englishName, int releasePrice, String modelNumber, String releaseDate) {
        this.name = name;
        this.englishName = englishName;
        this.releasePrice = releasePrice;
        this.modelNumber = modelNumber;
        this.releaseDate = releaseDate;
    }
}
