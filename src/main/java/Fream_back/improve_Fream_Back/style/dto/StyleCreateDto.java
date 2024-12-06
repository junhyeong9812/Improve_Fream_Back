package Fream_back.improve_Fream_Back.style.dto;

import lombok.Data;

@Data
public class StyleCreateDto {
    private Long userId;
    private Long orderItemId;
    private String content;
    private Integer rating;
    private String tempFilePath; // 임시 저장된 파일 경로
}
