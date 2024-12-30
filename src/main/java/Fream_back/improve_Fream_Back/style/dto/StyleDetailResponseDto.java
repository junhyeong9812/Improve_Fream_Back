package Fream_back.improve_Fream_Back.style.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class StyleDetailResponseDto {
    private Long id; // Style ID
    private String profileName; // 작성자 이름
    private String profileImageUrl; // 작성자 프로필 이미지
    private String content; // 텍스트 컨텐츠
    private String mediaUrl; // 미디어 URL
    private Long likeCount; // 좋아요 수
    private Long commentCount; // 댓글 수
    private String productName; // 상품명
    private String productEnglishName; // 상품 영어명
    private String thumbnailImageUrl; // 대표 이미지
    private Integer minSalePrice; // 최저 판매가
}
