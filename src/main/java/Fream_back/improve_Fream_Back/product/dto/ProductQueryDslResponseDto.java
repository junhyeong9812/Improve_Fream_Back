package Fream_back.improve_Fream_Back.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductQueryDslResponseDto {
    private Long id;
    private String name;
    private String brand;
    private Long mainCategoryId; // 메인 카테고리 ID 추가
    private String mainCategoryName;
    private Long subCategoryId;  // 서브 카테고리 ID 추가
    private String subCategoryName;
    private List<String> colors; // 색상을 배열로 그룹화
    private List<String> sizes;  // 사이즈를 배열로 그룹화
    private int quantity;



}