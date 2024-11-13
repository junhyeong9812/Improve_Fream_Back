package Fream_back.improve_Fream_Back.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordResetRequestDto {
    private String loginId; // 아이디
    private String phoneNumber; // 연락처 번호
    private String email; // 이메일

    private PasswordResetRequestDto(String loginId, String phoneNumber, String email) {
        this.loginId = loginId;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // 연락처로 생성하는 정적 팩토리 메서드
    public static PasswordResetRequestDto fromPhoneNumber(String loginId, String phoneNumber) {
        return new PasswordResetRequestDto(loginId, phoneNumber, null);
    }

    // 이메일로 생성하는 정적 팩토리 메서드
    public static PasswordResetRequestDto fromEmail(String loginId, String email) {
        return new PasswordResetRequestDto(loginId, null, email);
    }

}
