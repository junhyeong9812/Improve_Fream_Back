package Fream_back.improve_Fream_Back.product.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductColorUpdateRequestDto {
    private String colorName; // 색상명
    private String content; // 상세 설명
    private List<String> existingImages; // 유지할 기존 이미지 URL 목록
    private List<String> existingDetailImages; // 유지할 기존 상세 이미지 URL 목록
    private List<String> sizes; // 유지할 사이즈 목록
}
