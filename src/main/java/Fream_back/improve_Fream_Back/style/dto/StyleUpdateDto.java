package Fream_back.improve_Fream_Back.style.dto;

import lombok.Data;

@Data
public class StyleUpdateDto {
    private Long userId;
    private String content;
    private Integer rating;
    private String tempFilePath; // 새 파일 경로
}
