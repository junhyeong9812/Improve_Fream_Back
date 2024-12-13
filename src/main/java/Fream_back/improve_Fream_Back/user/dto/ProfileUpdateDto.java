package Fream_back.improve_Fream_Back.user.dto;

import lombok.Data;

@Data
public class ProfileUpdateDto {
    private String profileImage;      // 새로운 프로필 이미지 경로
    private String profileName;       // 프로필 이름
    private String realName;          // 이름
    private String bio;               // 소개글
    private Boolean isPublic;         // 공개 여부
}
