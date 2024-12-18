package Fream_back.improve_Fream_Back.user.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileUpdateDto {
    private String profileName;       // 프로필 이름
    private String realName;          // 이름
    private String bio;               // 소개글
    private Boolean isPublic;         // 공개 여부
}
