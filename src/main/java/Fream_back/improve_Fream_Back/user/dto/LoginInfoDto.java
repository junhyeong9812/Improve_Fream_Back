package Fream_back.improve_Fream_Back.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfoDto {
    private String email;
    private String phoneNumber;
    private String shoeSize;
    private Boolean optionalPrivacyAgreement;
    private Boolean smsConsent;
    private Boolean emailConsent;
}