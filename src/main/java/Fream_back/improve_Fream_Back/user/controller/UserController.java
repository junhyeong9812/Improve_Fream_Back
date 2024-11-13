package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        Optional<User> user = userService.login(loginDto);
        if (user.isPresent()) {
            User foundUser = user.get();

            // 쿠키 생성
            Cookie cookie = new Cookie("loginId", foundUser.getLoginId());
            cookie.setMaxAge(30 * 60); // 30분
            cookie.setHttpOnly(true); // JavaScript에서 접근 불가
            cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능

            // 쿠키를 응답에 추가
            response.addCookie(cookie);

            return ResponseEntity.ok(new LoginResponseDto(
                    "Login successful.",
                    foundUser.getLoginId(),
                    foundUser.getNickname()
            ));
        } else {
            return ResponseEntity.status(401).body(new LoginResponseDto("Invalid credentials.", null, null));
        }
    }

    // 전화번호로 아이디 찾기 엔드포인트
    @PostMapping("/find-loginId/phone")
    public ResponseEntity<UsernameResponseDto> findLoginIdByPhoneNumber(@RequestBody LoginIdRecoveryDto recoveryDto) {
        return userService.findLoginIdByPhoneNumber(recoveryDto)
                .map(loginId -> ResponseEntity.ok(new UsernameResponseDto(loginId)))
                .orElse(ResponseEntity.status(404).body(null));
    }

    // 이메일로 아이디 찾기 엔드포인트
    @PostMapping("/find-loginId/email")
    public ResponseEntity<UsernameResponseDto> findLoginIdByEmail(@RequestBody LoginIdRecoveryDto recoveryDto) {
        return userService.findLoginIdByEmail(recoveryDto)
                .map(loginId -> ResponseEntity.ok(new UsernameResponseDto(loginId)))
                .orElse(ResponseEntity.status(404).body(null));
    }

    // 비밀번호 재설정 요청 엔드포인트
    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequestDto requestDto) {
        boolean isValidUser = userService.validateUserForPasswordReset(requestDto);
        return isValidUser
                ? ResponseEntity.ok("User validated for password reset.")
                : ResponseEntity.status(404).body("User not found or invalid details.");
    }

    // 비밀번호 업데이트 엔드포인트
    @PostMapping("/password-reset/update")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateDto updateDto) {
        boolean isUpdated = userService.updatePassword(updateDto);
        return isUpdated
                ? ResponseEntity.ok("Password updated successfully.")
                : ResponseEntity.status(404).body("User not found.");
    }
}
