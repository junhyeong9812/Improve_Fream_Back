package Fream_back.improve_Fream_Back.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDto {
    private String loginId; // 로그인용 아이디
    private String password; // 로그인용 비밀번호

    public LoginDto(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}