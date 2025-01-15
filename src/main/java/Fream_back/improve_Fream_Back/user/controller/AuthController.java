package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.Jwt.TokenDto;
import Fream_back.improve_Fream_Back.user.entity.Gender;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.redis.RedisService;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final UserQueryService userQueryService;

    public AuthController(JwtTokenProvider jwtTokenProvider, RedisService redisService,UserQueryService userQueryService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.userQueryService =userQueryService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("RefreshToken") String refreshTokenHeader) {
        // RefreshToken 헤더에서 "Bearer " 제거
        String refreshToken = refreshTokenHeader.replace("Bearer ", "");

        // 1) Refresh Token 유효성 검증
        boolean valid = jwtTokenProvider.validateToken(refreshToken);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token is invalid or expired");
        }

        // 2) Redis 화이트리스트(또는 refresh 전용 저장소)에 존재하는지 확인
        if (!redisService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token is not in the whitelist");
        }

        // 3) JWT에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // (Optional) "이메일이 실제 DB 사용자와 매칭되는가?" 등의 검증 로직

        // 4) 새 Access Token 발급
        User user = userQueryService.findByEmail(email);
        Integer age =user.getAge();    // DB나 Redis에서 가져옴
        Gender gender = user.getGender();  // DB나 Redis에서 가져옴
        TokenDto tokenDto = jwtTokenProvider.generateTokenPair(email, age, gender);

        return ResponseEntity.ok(tokenDto);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader(value = "RefreshToken", required = false) String refreshTokenHeader
    ) {
        try {
            // 1) Access Token 추출
            String accessToken = authorizationHeader.replace("Bearer ", "");

            // 2) Refresh Token도 추출(선택적으로)
            String refreshToken = (refreshTokenHeader != null)
                    ? refreshTokenHeader.replace("Bearer ", "")
                    : null;

            // 3) 유효한 토큰인지 검증(옵션)
            //    - 꼭 검증하지 않아도, 무효화만 하고 끝낼 수도 있음
            boolean accessValid = jwtTokenProvider.validateToken(accessToken);
            if (!accessValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Access Token is invalid or expired");
            }

            // 4) Redis에서 삭제
            redisService.removeAccessToken(accessToken);
            if (refreshToken != null && !refreshToken.isBlank()) {
                redisService.removeRefreshToken(refreshToken);
            }

            // 5) 응답
            return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }
}