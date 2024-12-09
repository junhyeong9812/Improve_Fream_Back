package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import Fream_back.improve_Fream_Back.user.service.UserService;
import Fream_back.improve_Fream_Back.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("testLoginId", "password", "testNickname", "John Doe", "123-4567-8901", "test@example.com", true, true, Role.USER);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccess() {
        LoginDto loginDto = new LoginDto("testLoginId", "password");
        when(userRepository.findByLoginIdAndPassword(anyString(), anyString())).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.login(loginDto);
        assertThat(result).isPresent();
        assertThat(result.get().getLoginId()).isEqualTo("testLoginId");
    }

    @Test
    @DisplayName("전화번호로 아이디 찾기 테스트")
    public void findLoginIdByPhoneNumber() {
        LoginIdRecoveryDto recoveryDto = LoginIdRecoveryDto.fromPhoneNumber("123-4567-8901");
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(testUser));

        Optional<String> result = userService.findLoginIdByPhoneNumber(recoveryDto);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("testLoginId");
    }

    @Test
    @DisplayName("이메일로 아이디 찾기 테스트")
    public void findLoginIdByEmail() {
        LoginIdRecoveryDto recoveryDto = LoginIdRecoveryDto.fromEmail("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        Optional<String> result = userService.findLoginIdByEmail(recoveryDto);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("testLoginId");
    }

    @Test
    @DisplayName("전화번호로 비밀번호 재설정 인증 테스트")
    public void validateUserForPasswordResetWithPhoneNumber() {
        PasswordResetRequestDto requestDto = PasswordResetRequestDto.fromPhoneNumber("testLoginId", "123-4567-8901");

        // 목 객체 설정 - 정확한 매개변수 값 사용
        when(userRepository.findByLoginIdAndPhoneNumberOrEmail(
                "testLoginId",
                "123-4567-8901",
                null // 이메일은 null로 전달
        )).thenReturn(Optional.of(testUser));

        boolean isValid = userService.validateUserForPasswordReset(requestDto);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("이메일로 비밀번호 재설정 인증 테스트")
    public void validateUserForPasswordResetWithEmail() {
        PasswordResetRequestDto requestDto = PasswordResetRequestDto.fromEmail("testLoginId", "test@example.com");

        // 목 객체 설정 - 정확한 매개변수 값 사용
        when(userRepository.findByLoginIdAndPhoneNumberOrEmail(
                "testLoginId",
                null, // 전화번호는 null로 전달
                "test@example.com"
        )).thenReturn(Optional.of(testUser));

        boolean isValid = userService.validateUserForPasswordReset(requestDto);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("비밀번호 업데이트 테스트")
    public void updatePassword() {
        PasswordUpdateDto updateDto = new PasswordUpdateDto("testLoginId", "newPassword");
        when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(testUser));

        boolean isUpdated = userService.updatePassword(updateDto);
        assertThat(isUpdated).isTrue();
        assertThat(testUser.getPassword()).isEqualTo("newPassword");
    }

    @Test
    @DisplayName("LoginId 중복 체크 - 중복일 경우")
    public void checkDuplicateLoginIdWhenExists() {
        // given
        String duplicateLoginId = "testLoginId";
        when(userRepository.existsByLoginId(duplicateLoginId)).thenReturn(true);

        // when
        String result = userService.checkDuplicateLoginId(duplicateLoginId);

        // then
        assertThat(result).isEqualTo("duplicate");
    }

    @Test
    @DisplayName("LoginId 중복 체크 - 중복이 아닐 경우")
    public void checkDuplicateLoginIdWhenNotExists() {
        // given
        String uniqueLoginId = "uniqueLoginId";
        when(userRepository.existsByLoginId(uniqueLoginId)).thenReturn(false);

        // when
        String result = userService.checkDuplicateLoginId(uniqueLoginId);

        // then
        assertThat(result).isEqualTo("ok");
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void registerNewUser() {
        // given
        UserSignupDto signupDto = new UserSignupDto("newLoginId", "newPassword", "newNickname", "Jane Doe", "987-6543-2101", "new@example.com", false, true);

        // Mock 설정 - userRepository.save() 호출 시, 전달받은 User 객체를 그대로 반환
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = userService.registerUser(signupDto);

        // then
        assertThat(result.getLoginId()).isEqualTo("newLoginId");
        assertThat(result.getNickname()).isEqualTo("newNickname");
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }
}