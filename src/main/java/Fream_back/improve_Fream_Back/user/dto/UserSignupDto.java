package Fream_back.improve_Fream_Back.user.dto;

import Fream_back.improve_Fream_Back.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupDto {
    private String loginId;
    private String password;
    private String nickname;
    private String realName;
    private String phoneNumber;
    private String email;
    private Boolean phoneNotificationConsent = false;
    private Boolean emailNotificationConsent = false;
}
