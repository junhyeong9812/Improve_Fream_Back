package Fream_back.improve_Fream_Back.style.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StyleSearchDto {
    private Long userId;      // 검색할 사용자 ID
    private Long productId;   // 검색할 상품 ID
    private String keyword;   // 검색어
}
