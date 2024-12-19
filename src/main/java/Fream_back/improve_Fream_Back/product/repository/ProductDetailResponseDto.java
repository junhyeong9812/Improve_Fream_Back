package Fream_back.improve_Fream_Back.product.repository;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDto {
    private Long id; // 상품 ID
    private String name; // 상품명
    private String englishName; // 상품 영어명
    private int releasePrice; // 발매가
    private String thumbnailImageUrl; // 대표 이미지 URL
    private String colorName; // 색상명
    private String content; // 색상 상세 설명
    private List<SizeDetailDto> sizes; // 사이즈 정보 리스트

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SizeDetailDto {
        private String size; // 사이즈 이름
        private int purchasePrice; // 구매가
        private int salePrice; // 판매가
        private int quantity; // 재고 수량
    }
}

