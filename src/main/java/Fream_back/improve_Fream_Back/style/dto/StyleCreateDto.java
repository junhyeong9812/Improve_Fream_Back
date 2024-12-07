package Fream_back.improve_Fream_Back.style.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StyleCreateDto {
    private Long userId;
    private Long orderItemId;
    private String content;
    private Integer rating;
    private String tempFilePath; // 임시 저장된 파일 경로
}
