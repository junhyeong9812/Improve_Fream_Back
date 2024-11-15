package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.dto.UserSignupDto;
import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.redis.RedisService;
import Fream_back.improve_Fream_Back.user.service.UserService;
import Fream_back.improve_Fream_Back.user.dto.LoginDto;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class JwtUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserController userController;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    private User testUser;

    private String jwtToken;

    @BeforeEach
    public void setUp() {
        // given: 테스트에 사용할 가상의 사용자 객체를 준비합니다.
        testUser = User.builder()
                .loginId("testUser")
                .password(passwordEncoder.encode("password123"))  // 비밀번호는 암호화하여 설정
                .nickname("testNickname")
                .realName("testRealName")
                .phoneNumber("1234567890")
                .email("test@example.com")
                .phoneNotificationConsent(true)
                .emailNotificationConsent(true)
                .role(Role.USER)  // ROLE_USER로 설정
                .build();

        // 사용자 서비스에 사용자 저장 (실제 데이터베이스에 저장되는지 확인하려면 repository 사용)
        userService.registerUser(new UserSignupDto(
                testUser.getLoginId(),
                "password123",
                testUser.getNickname(),
                testUser.getRealName(),
                testUser.getPhoneNumber(),
                testUser.getEmail()
        ));

//         로그인 후 JWT 토큰 생성
        LoginDto loginDto = new LoginDto("testUser", "password123");
        Optional<User> userOpt = userService.login(loginDto);
        jwtToken = userOpt.isPresent() ? jwtTokenProvider.generateToken(userOpt.get().getLoginId()) : null;

        // 로그인 후 Redis에 토큰이 등록되었는지 확인
        boolean isTokenInRedis = redisService.isTokenInWhitelist(jwtToken);
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰과 사용자 정보 반환")
    public void testLoginSuccess() throws Exception {
        // given: 로그인 요청 DTO
        LoginDto loginDto = new LoginDto("testUser", "password123");

        // when: 로그인 서비스에서 사용자 확인 (목(mock)을 사용하거나 실제 DB에서 확인)
        Optional<User> userOpt = userService.login(loginDto);

        // 사용자가 존재하는지 확인
        assertTrue(userOpt.isPresent(), "로그인할 사용자가 존재하지 않습니다.");

        // 토큰 발급
        String token = jwtTokenProvider.generateToken(userOpt.get().getLoginId());

        System.out.println("token = " + token);
        System.out.println(" post요청 시작 ");
        // then: 로그인 성공 응답을 확인
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginId\":\"testUser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful."))
                .andExpect(jsonPath("$.loginId").value(userOpt.get().getLoginId()))
                .andExpect(jsonPath("$.nickname").value(userOpt.get().getNickname()))
//                .andExpect(jsonPath("$.token").value(token));  // 서버가 반환하는 token 값 확인
                .andReturn()
                .getResponse()
                .getHeader("Authorization");  // 헤더에서 토큰을 추출

        // then: Redis에 토큰이 등록되었는지 확인
        boolean isTokenInRedis = redisService.isTokenInWhitelist(token);
        assertTrue(isTokenInRedis, "로그인 후 Redis에 토큰이 등록되지 않았습니다.");
    }

    @Test
    @DisplayName("로그인 실패 시 401 오류와 메시지 반환")
    public void testLoginFailure() throws Exception {
        // given: 잘못된 로그인 정보
        LoginDto loginDto = new LoginDto("wrongUser", "wrongPassword");

        // when: 로그인 서비스에서 사용자 확인 (실제 로그인 동작)
        Optional<User> userOpt = userService.login(loginDto);

        // then: 로그인 실패 응답을 확인
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginId\":\"wrongUser\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials."));
    }

    @Test
    @DisplayName("로그아웃 성공 시 JWT 토큰을 화이트리스트에서 제거")
    public void testLogoutSuccess() throws Exception {
        // given: 유효한 토큰
        String token = jwtToken;

        // when: 로그아웃 요청 시
        mockMvc.perform(post("/api/users/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful."));

        // then: Redis에서 해당 토큰이 삭제되었는지 확인
        // (이 부분은 실제 Redis 연결이나 mock을 사용하여 확인 가능)
        boolean isTokenRemoved = redisService.isTokenRemoved(token); // 예시 메서드
        assertTrue(isTokenRemoved, "Redis에서 토큰이 삭제되지 않았습니다.");
    }

    @Test
    @DisplayName("로그아웃 실패 시 Authorization 헤더 없으면 400 오류 반환")
    public void testLogoutFailureWithoutAuthorizationHeader() throws Exception {
        // when: Authorization 헤더 없이 로그아웃 요청
        mockMvc.perform(post("/api/users/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Authorization header is missing or invalid."));
    }

    @Test
    @DisplayName("잘못된 토큰으로 로그아웃 시 400 오류 반환")
    public void testLogoutFailureWithInvalidToken() throws Exception {
        // given: 잘못된 토큰
        String invalidToken = "invalid-jwt-token";

        // when: 잘못된 토큰으로 로그아웃 요청
        mockMvc.perform(post("/api/users/logout")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token."));

        // then: Redis에서 잘못된 토큰 처리 시도하지 않음

    }
}