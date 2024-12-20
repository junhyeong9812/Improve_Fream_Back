package Fream_back.improve_Fream_Back.product.repository;

import lombok.*;

@Data
@AllArgsConstructor
public class SortOption {
    private String field;  // 정렬 기준 (price, releaseDate, interestCount 등)
    private String order;  // 정렬 순서 (asc, desc)
}