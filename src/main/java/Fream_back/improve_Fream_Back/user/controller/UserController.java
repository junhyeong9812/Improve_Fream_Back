package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final UserQueryService userQueryService;
    private final PasswordResetService passwordResetService;
    private final UserUpdateService userUpdateService;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 주입
    private final UserCommandService userCommandService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody @Validated UserRegistrationDto dto) {
        try {
            User user = userCommandService.registerUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("status", "success", "userEmail", user.getEmail()));
        } catch (IllegalArgumentException e) {
            // 사용자 입력이 잘못된 경우 - 400 Bad Request
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            // 서버 내부 오류 - 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "회원가입 처리 중 문제가 발생했습니다."));
        }
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            String token = authService.login(loginRequestDto);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "로그인 처리 중 문제가 발생했습니다."));
        }
    }
    
    //이메일 찾기 
    @PostMapping("/find-email")
    public ResponseEntity<Map<String, String>> findEmail(@RequestBody EmailFindRequestDto emailFindRequestDto) {
        try {
            String email = userQueryService.findEmailByPhoneNumber(emailFindRequestDto);
            return ResponseEntity.ok(Map.of("email", email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "이메일 찾기 처리 중 문제가 발생했습니다."));
        }
    }

    // 비밀번호 찾기 - 사용자 확인
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPasswordEligibility(@RequestBody PasswordResetRequestDto dto) {
        try {
            boolean eligible = passwordResetService.checkPasswordResetEligibility(dto.getEmail(), dto.getPhoneNumber());
            if (eligible) {
                return ResponseEntity.ok(Map.of("status", "ok"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "해당 이메일 및 전화번호로 사용자를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "비밀번호 찾기 처리 중 문제가 발생했습니다."));
        }
    }

    // 비밀번호 변경
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDto requestDto) {
        try {
            boolean isReset = passwordResetService.resetPassword(requestDto);

            if (isReset) {
                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("비밀번호 변경 중 문제가 발생했습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 로그인 정보 변경
    @PutMapping("/update-login-info")
    public ResponseEntity<Map<String, String>> updateLoginInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Validated LoginInfoUpdateDto dto) {
        try {
            // Authorization 헤더에서 토큰 추출 (Bearer 제거)
            String token = authorizationHeader.replace("Bearer ", "");

            // 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);

            // 로그인 정보 업데이트 서비스 호출
            userUpdateService.updateLoginInfo(email, dto);

            // 성공 메시지 반환
            return ResponseEntity.ok(Map.of("status", "success", "message", "로그인 정보가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            // 잘못된 입력 값 처리
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            // 서버 내부 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "로그인 정보 변경 처리 중 문제가 발생했습니다."));
        }
    }

    //로그인 정보 조회
    @GetMapping("/login-info")
    public ResponseEntity<LoginInfoDto> getLoginInfo(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String email = jwtTokenProvider.getEmailFromToken(token);

            LoginInfoDto loginInfoDto = userQueryService.getLoginInfo(email);
            return ResponseEntity.ok(loginInfoDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    //회원 삭제
    @DeleteMapping("/delete-account")
    public ResponseEntity<Map<String, String>> deleteAccount(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String email = jwtTokenProvider.getEmailFromToken(token);

            userCommandService.deleteAccount(email);

            return ResponseEntity.ok(Map.of("status", "success", "message", "회원 탈퇴가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "회원 탈퇴 처리 중 문제가 발생했습니다."));
        }
    }


}