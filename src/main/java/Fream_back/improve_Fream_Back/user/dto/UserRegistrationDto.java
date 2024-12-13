package Fream_back.improve_Fream_Back.user.dto;

import Fream_back.improve_Fream_Back.user.entity.ShoeSize;


import lombok.Data;


@Data
public class UserRegistrationDto {

    private String email; // 이메일 주소

    private String password; // 비밀번호

    private String phoneNumber; // 선택적 필드로 설정

    private String referralCode; // 추천인 코드 (선택)

    private ShoeSize shoeSize; // 신발 사이즈

    private Boolean isOver14; // 만 14세 이상 확인

    private Boolean termsAgreement; // 이용약관 동의

    private Boolean privacyAgreement; // 개인정보 수집 및 이용 동의 (필수)

    private Boolean optionalPrivacyAgreement; // 개인정보 수집 및 이용 동의 (선택)

    private Boolean adConsent; // 광고성 정보 수신 동의 (선택)
}