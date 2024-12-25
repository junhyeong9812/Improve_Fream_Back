package Fream_back.improve_Fream_Back.product.dto;

import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.repository.SortOption;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchDto {
    private String keyword;
    private List<Long> categoryIds;
    private List<GenderType> genders;
    private List<Long> brandIds;
    private List<Long> collectionIds;
    private List<String> colors;
    private List<String> sizes;
    private Integer minPrice;
    private Integer maxPrice;
    private SortOption sortOption;

    // 유효성 검증 로직
    public void validate() {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("최소 가격이 최대 가격보다 클 수 없습니다.");
        }
        // 기타 유효성 검증 로직...
    }
}