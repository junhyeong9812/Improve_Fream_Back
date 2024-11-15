package Fream_back.improve_Fream_Back.product.entity;

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
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl; // 이미지 파일 경로 혹은 URL

    private String imageType; // 썸네일인지 상세 페이지용인지 구분

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private boolean isMainThumbnail; // 메인 썸네일 여부

    public ProductImage(String imageUrl, String imageType, boolean isMainThumbnail) {
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.isMainThumbnail = isMainThumbnail;
    }

    // 연관관계 편의 메서드
    public void assignProduct(Product product) {
        this.product = product;
    }
}
