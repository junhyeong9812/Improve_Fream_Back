package Fream_back.improve_Fream_Back.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginIdRecoveryDto { // 이름을 LoginIdRecoveryDto로 변경 (선택 사항)
    private String phoneNumber; // 연락처 번호
    private String email; // 이메일 (이메일 인증 사용 시)
    // 연락처를 통한 아이디 찾기 요청 생성자
    // private 생성자를 통해 외부에서 직접 호출하지 못하도록 설정
    private LoginIdRecoveryDto(String phoneNumber, String email) {
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // 연락처로 LoginIdRecoveryDto 생성
    public static LoginIdRecoveryDto fromPhoneNumber(String phoneNumber) {
        return new LoginIdRecoveryDto(phoneNumber, null);
    }

    // 이메일로 LoginIdRecoveryDto 생성
    public static LoginIdRecoveryDto fromEmail(String email) {
        return new LoginIdRecoveryDto(null, email);
    }
}