package Fream_back.improve_Fream_Back.user;

import Fream_back.improve_Fream_Back.user.config.EndUserTestConfig;
import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.ShoeSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(EndUserTestConfig.class) // TestConfig를 로드하여 초기 데이터를 삽입
class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    private String token;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;

        // 로그인하여 토큰 발급받기
        LoginRequestDto loginDto = new LoginRequestDto("user1@example.com", "password123");
        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                baseUrl + "/api/users/login",
                loginDto,
                Map.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        token = "Bearer " + loginResponse.getBody().get("token");
    }

    @Test
    @DisplayName("회원가입 성공")
    void registerUserSuccess() {
        UserRegistrationDto dto = UserRegistrationDto.builder()
                .email("test@example.com")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .shoeSize(ShoeSize.SIZE_270)
                .isOver14(true)
                .termsAgreement(true)
                .privacyAgreement(true)
                .optionalPrivacyAgreement(true)
                .adConsent(false)
                .build();

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/users/register",
                dto,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("status", "success");
        assertThat(response.getBody()).containsEntry("userEmail", "test@example.com");
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        LoginRequestDto dto = new LoginRequestDto("user1@example.com", "password123");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/users/login",
                dto,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteAccountSuccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/users/delete-account",
                HttpMethod.DELETE,
                request,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "success");
        assertThat(response.getBody()).containsEntry("message", "회원 탈퇴가 완료되었습니다.");
    }
}
