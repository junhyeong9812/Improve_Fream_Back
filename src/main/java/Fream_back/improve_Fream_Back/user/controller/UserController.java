package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.redis.RedisService;
import Fream_back.improve_Fream_Back.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인 엔드포인트
     * 사용자의 loginId와 password로 로그인 처리.
     * 성공 시 JWT 토큰을 클라이언트에게 응답.
     *
     * @param loginDto 로그인에 필요한 loginId와 password 정보를 담은 DTO
     * @return 로그인 성공 시 JWT 토큰과 사용자 정보를 반환, 실패 시 오류 메시지 반환
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {

        Optional<User> user = userService.login(loginDto);
        // user가 실제로 존재하는지 확인
//        System.out.println("컨트롤러에서 받은 user: " + user); // 전체 Optional 객체 출력

        if (user.isPresent()) {

            User foundUser = user.get();

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(foundUser.getLoginId());

            // 토큰을 Redis 화이트리스트에 저장
            redisService.addTokenToWhitelist(token);

            // JWT 토큰을 헤더에 추가
            response.setHeader("Authorization", "Bearer " + token);

            // JWT 토큰을 클라이언트에게 응답
            return ResponseEntity.ok(new LoginResponseDto(
                    "Login successful.",
                    foundUser.getLoginId(),
                    foundUser.getNickname(),
//                    token  // JWT 토큰을 반환
                    null // 토큰은 본문에서 제외
            ));
        } else {
            return ResponseEntity.status(401).body(new LoginResponseDto("Invalid credentials.", null, null, null));
        }
    }

    /**
     * 로그아웃 엔드포인트
     * 클라이언트에서 전달된 JWT 토큰을 화이트리스트에서 제거하여 로그아웃 처리.
     *
     * @param request HTTP 요청에서 Authorization 헤더를 통해 토큰을 받음
     * @return 로그아웃 성공 메시지 또는 실패 메시지 반환
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 부분을 제외한 토큰만 추출

            // Redis에서 토큰이 화이트리스트에 있는지 확인
            if (!redisService.isTokenInWhitelist(token)) {
                return ResponseEntity.status(400).body("Invalid token.");
            }

            // Redis에서 토큰 제거
            redisService.removeTokenFromWhitelist(token);

            return ResponseEntity.ok("Logout successful.");
        }

        return ResponseEntity.status(400).body("Authorization header is missing or invalid.");
    }

    /**
     * 전화번호로 아이디 찾기 엔드포인트
     * 사용자의 전화번호로 loginId를 조회.
     *
     * @param recoveryDto 전화번호 정보를 담은 LoginIdRecoveryDto
     * @return loginId가 존재하면 반환, 없으면 404 오류 응답
     */
    @PostMapping("/find-loginId/phone")
    public ResponseEntity<UsernameResponseDto> findLoginIdByPhoneNumber(@RequestBody LoginIdRecoveryDto recoveryDto) {
        return userService.findLoginIdByPhoneNumber(recoveryDto)
                .map(loginId -> ResponseEntity.ok(new UsernameResponseDto(loginId)))
                .orElse(ResponseEntity.status(404).body(null));
    }

    /**
     * 이메일로 아이디 찾기 엔드포인트
     * 사용자의 이메일로 loginId를 조회.
     *
     * @param recoveryDto 이메일 정보를 담은 LoginIdRecoveryDto
     * @return loginId가 존재하면 반환, 없으면 404 오류 응답
     */
    @PostMapping("/find-loginId/email")
    public ResponseEntity<UsernameResponseDto> findLoginIdByEmail(@RequestBody LoginIdRecoveryDto recoveryDto) {
        return userService.findLoginIdByEmail(recoveryDto)
                .map(loginId -> ResponseEntity.ok(new UsernameResponseDto(loginId)))
                .orElse(ResponseEntity.status(404).body(null));
    }

    /**
     * 비밀번호 재설정 요청 엔드포인트
     * 사용자가 입력한 정보가 유효한지 확인.
     * 사용자가 입력한 loginId와 전화번호 또는 이메일이 일치하는 경우 비밀번호 재설정을 승인.
     *
     * @param requestDto loginId, 전화번호, 이메일 정보를 포함한 비밀번호 재설정 요청 DTO
     * @return 유효한 사용자일 경우 승인 메시지 반환, 아니면 404 오류 응답
     */
    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequestDto requestDto) {
        boolean isValidUser = userService.validateUserForPasswordReset(requestDto);
        return isValidUser
                ? ResponseEntity.ok("User validated for password reset.")
                : ResponseEntity.status(404).body("User not found or invalid details.");
    }

    /**
     * 비밀번호 업데이트 엔드포인트
     * 인증된 사용자에 대해 비밀번호를 업데이트.
     *
     * @param updateDto loginId와 새 비밀번호 정보를 포함한 PasswordUpdateDto
     * @return 업데이트 성공 시 성공 메시지 반환, 실패 시 404 오류 응답
     */
    @PostMapping("/password-reset/update")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateDto updateDto) {
        boolean isUpdated = userService.updatePassword(updateDto);
        return isUpdated
                ? ResponseEntity.ok("Password updated successfully.")
                : ResponseEntity.status(404).body("User not found.");
    }

    /**
     * 로그인 아이디 중복 확인 엔드포인트
     *
     * @param loginId 확인할 로그인 아이디
     * @return 중복 여부 ("ok" 또는 "duplicate")
     */
    @GetMapping("/check-duplicate")
    public ResponseEntity<String> checkDuplicateLoginId(@RequestParam(name = "loginId") String loginId) {
        String result = userService.checkDuplicateLoginId(loginId);
        return ResponseEntity.ok(result);
    }

    /**
     * 회원 가입 엔드포인트
     *
     * @param signupDto 회원 가입에 필요한 정보가 담긴 UserSignupDto
     * @return 등록된 User 엔티티를 포함한 ResponseEntity
     */
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody UserSignupDto signupDto) {
        User newUser = userService.registerUser(signupDto);
        return ResponseEntity.ok(newUser);
    }

    // 필터에서 설정한 Authorization 헤더 확인 후, 인증 처리
    private boolean isTokenValid(HttpServletRequest request) {
        String tokenStatus = (String) request.getAttribute("Authorization");

        // 토큰 상태에 따라 인증 실패 처리
        if (tokenStatus == null || tokenStatus.startsWith("Token") || tokenStatus.equals("Invalid or expired token")) {
            return false;
        }
        return true;
    }
}
