package Fream_back.improve_Fream_Back.style.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StyleResponseDto {
    private Long id;                // 스타일 ID
    private String content;         // 스타일 내용
    private Integer rating;         // 별점
    private String imageUrl;        // 이미지 URL
    private String videoUrl;        // 동영상 URL
    private LocalDateTime createdDate; // 생성일

    // 유저 정보
    private String userNickname;    // 작성자 닉네임

    // 상품 정보
    private Long productId;         // 상품 ID
    private String productName;     // 상품명
    private String productBrand;    // 브랜드명
    private String productImageUrl; // 메인 썸네일 이미지 URL
}
