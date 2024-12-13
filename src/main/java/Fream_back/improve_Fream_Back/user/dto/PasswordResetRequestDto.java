package Fream_back.improve_Fream_Back.user.dto;

import lombok.Data;

@Data
public class PasswordResetRequestDto {
    private String email;
    private String phoneNumber;
    private String newPassword; // 새로운 비밀번호
    private String confirmPassword; // 비밀번호 확인

    // 비밀번호와 확인용 비밀번호가 일치하는지 검증
    public void validatePasswords() {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }
}
