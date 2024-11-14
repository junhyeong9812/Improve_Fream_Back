package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserService;
import Fream_back.improve_Fream_Back.user.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class MockUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final User testUser = new User("testLoginId", "password", "testNickname", "John Doe", "123-4567-8901", "test@example.com", true, true, Role.USER);

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccess() throws Exception {
        LoginDto loginDto = new LoginDto("testLoginId", "password");
        when(userService.login(any(LoginDto.class))).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful."))
                .andExpect(jsonPath("$.loginId").value("testLoginId"))
                .andExpect(jsonPath("$.nickname").value("testNickname"))
                .andExpect(cookie().exists("loginId"));
    }

    @Test
    @DisplayName("전화번호로 아이디 찾기 테스트")
    public void findLoginIdByPhoneNumber() throws Exception {
        LoginIdRecoveryDto recoveryDto = LoginIdRecoveryDto.fromPhoneNumber("123-4567-8901");
        when(userService.findLoginIdByPhoneNumber(any(LoginIdRecoveryDto.class))).thenReturn(Optional.of("testLoginId"));

        mockMvc.perform(post("/api/users/find-loginId/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recoveryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("testLoginId"));
    }

    @Test
    @DisplayName("이메일로 아이디 찾기 테스트")
    public void findLoginIdByEmail() throws Exception {
        LoginIdRecoveryDto recoveryDto = LoginIdRecoveryDto.fromEmail("test@example.com");
        when(userService.findLoginIdByEmail(any(LoginIdRecoveryDto.class))).thenReturn(Optional.of("testLoginId"));

        mockMvc.perform(post("/api/users/find-loginId/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recoveryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("testLoginId"));
    }

    @Test
    @DisplayName("전화번호로 비밀번호 재설정 인증 테스트")
    public void validateUserForPasswordResetWithPhoneNumber() throws Exception {
        PasswordResetRequestDto requestDto = PasswordResetRequestDto.fromPhoneNumber("testLoginId", "123-4567-8901");
        when(userService.validateUserForPasswordReset(any(PasswordResetRequestDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/users/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User validated for password reset."));
    }

    @Test
    @DisplayName("이메일로 비밀번호 재설정 인증 테스트")
    public void validateUserForPasswordResetWithEmail() throws Exception {
        PasswordResetRequestDto requestDto = PasswordResetRequestDto.fromEmail("testLoginId", "test@example.com");
        when(userService.validateUserForPasswordReset(any(PasswordResetRequestDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/users/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User validated for password reset."));
    }

    @Test
    @DisplayName("비밀번호 업데이트 테스트")
    public void updatePassword() throws Exception {
        PasswordUpdateDto updateDto = new PasswordUpdateDto("testLoginId", "newPassword");
        when(userService.updatePassword(any(PasswordUpdateDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/users/password-reset/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Password updated successfully."));
    }

    @Test
    @DisplayName("로그인 아이디 중복 확인 - 중복일 경우")
    public void checkDuplicateLoginId_duplicate() throws Exception {
        // given
        String duplicateLoginId = "duplicateLoginId";
        when(userService.checkDuplicateLoginId(anyString())).thenReturn("duplicate");

        // when & then
        mockMvc.perform(get("/api/users/check-duplicate")
                        .param("loginId", duplicateLoginId))
                .andExpect(status().isOk())
                .andExpect(content().string("duplicate"));
    }

    @Test
    @DisplayName("로그인 아이디 중복 확인 - 중복이 아닐 경우")
    public void checkDuplicateLoginId_notDuplicate() throws Exception {
        // given
        String uniqueLoginId = "uniqueLoginId";
        when(userService.checkDuplicateLoginId(anyString())).thenReturn("ok");

        // when & then
        mockMvc.perform(get("/api/users/check-duplicate")
                        .param("loginId", uniqueLoginId))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")
    public void signupUserSuccess() throws Exception {
        // given
        UserSignupDto signupDto = new UserSignupDto("newLoginId", "newPassword", "newNickname", "Jane Doe", "987-6543-2101", "new@example.com", false, true);
        User createdUser = User.builder()
                .loginId("newLoginId")
                .password("newPassword")
                .nickname("newNickname")
                .realName("Jane Doe")
                .phoneNumber("987-6543-2101")
                .email("new@example.com")
                .phoneNotificationConsent(false)
                .emailNotificationConsent(true)
                .role(Role.USER)
                .build();

        // when
        when(userService.registerUser(any(UserSignupDto.class))).thenReturn(createdUser);

        // then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("newLoginId"))
                .andExpect(jsonPath("$.nickname").value("newNickname"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

}

