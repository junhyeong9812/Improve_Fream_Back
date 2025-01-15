package Fream_back.improve_Fream_Back.user.Jwt;

import Fream_back.improve_Fream_Back.user.entity.Gender;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Integer age;
    private Gender gender;

    // 필요한 정보를 더 추가할 수도 있음 (예: role, 권한 목록 등)
}