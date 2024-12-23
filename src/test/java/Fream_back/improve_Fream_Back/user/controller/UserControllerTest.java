package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.ShoeSize;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.redis.RedisService;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import Fream_back.improve_Fream_Back.user.security.CustomUserDetailsService;
import Fream_back.improve_Fream_Back.user.security.SecurityConfig;
import Fream_back.improve_Fream_Back.user.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class,TestConfig.class}) // SecurityConfig를 Import하여 CSRF 설정 반
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserQueryService userQueryService;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private UserUpdateService userUpdateService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserCommandService userCommandService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RedisService redisService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @DisplayName("회원가입 성공")
    void registerUserSuccess() throws Exception {
        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
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

        when(userCommandService.registerUser(any(UserRegistrationDto.class)))
                .thenReturn(User.builder().email("test@example.com").build());

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.userEmail").value("test@example.com"));
    }



    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password123");

        when(authService.login(any(LoginRequestDto.class))).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    @DisplayName("이메일 찾기 성공")
    void findEmailSuccess() throws Exception {
        EmailFindRequestDto emailFindRequestDto = new EmailFindRequestDto("010-1234-5678");

        when(userQueryService.findEmailByPhoneNumber(any(EmailFindRequestDto.class)))
                .thenReturn("user1@example.com");

        mockMvc.perform(post("/api/users/find-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailFindRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteAccountSuccess() throws Exception {
        String token = "Bearer mocked-jwt-token";

        when(jwtTokenProvider.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        Mockito.doNothing().when(userCommandService).deleteAccount(anyString());

        mockMvc.perform(delete("/api/users/delete-account")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("회원 탈퇴가 완료되었습니다."));
    }
}
