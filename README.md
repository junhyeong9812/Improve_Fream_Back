Fream 개선 프로젝트
<br>
기존의 Kream클론 프로젝트에서 부족한 점을 개선하기 위한 프로젝트
<br>
기존 프로젝트 : https://github.com/junhyeong9812/fream_back
<br>
기본 API
<br>
SpringBoot3.1
SpringDataJpa

<br>
기본 ERD구조
<br>
<img src="https://github.com/user-attachments/assets/c040190e-b830-451d-a873-f0c0640dc05f"/>
<br>

# API 명세서

---

## 1. 로그인 엔드포인트
- **URL**: `/api/users/login`
- **Method**: `POST`
- **Description**: 사용자의 `loginId`와 `password`로 로그인. 성공 시 쿠키에 `loginId`를 저장하고 사용자 정보를 반환.

### Request Body
```json
{
  "loginId": "string",
  "password": "string"
}
```



