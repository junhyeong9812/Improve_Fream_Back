package Fream_back.improve_Fream_Back.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordUpdateDto {
    private String loginId; // 사용자 아이디
    private String newPassword; // 새 비밀번호

    public PasswordUpdateDto(String loginId, String newPassword) {
        this.loginId = loginId;
        this.newPassword = newPassword;
    }
}
