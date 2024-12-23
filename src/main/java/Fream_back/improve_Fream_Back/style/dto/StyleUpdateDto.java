package Fream_back.improve_Fream_Back.style.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StyleUpdateDto {
    private Long userId;
    private String content;
    private Integer rating;
    private String tempFilePath; // 새 파일 경로
}
