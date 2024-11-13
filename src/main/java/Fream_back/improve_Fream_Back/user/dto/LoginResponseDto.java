package Fream_back.improve_Fream_Back.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {
    private String message; // 로그인 성공 메시지
    private String loginId; // 사용자 아이디
    private String nickname; // 사용자 별명

    public LoginResponseDto(String message, String loginId, String nickname) {
        this.message = message;
        this.loginId = loginId;
        this.nickname = nickname;
    }
}
