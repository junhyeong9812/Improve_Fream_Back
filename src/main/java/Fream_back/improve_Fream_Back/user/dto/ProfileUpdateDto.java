package Fream_back.improve_Fream_Back.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDto {
    private String profileName;       // 프로필 이름
    private String Name;          // 이름
    private String bio;               // 소개글
    private Boolean isPublic;         // 공개 여부
}
