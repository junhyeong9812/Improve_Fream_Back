package Fream_back.improve_Fream_Back.user.Jwt;

import Fream_back.improve_Fream_Back.user.redis.RedisService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;  // JWT 서명을 위한 비밀 키

    @Value("${jwt.expiration}")
    private long expirationTime;  // JWT 만료 시간 (밀리초 단위)

    private final RedisService redisService;

    // RedisService 주입
    public JwtTokenProvider(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * JWT 토큰을 생성하는 메서드
     * @param loginId JWT에 포함할 로그인 ID
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String loginId) {
        // JWT 토큰을 생성하여 반환
        return JWT.create()
                .withSubject(loginId)  // 로그인 ID를 JWT의 subject로 설정
                .withIssuedAt(new Date())  // 발급 시간을 현재 시간으로 설정
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))  // 만료 시간을 설정
                .sign(Algorithm.HMAC512(secretKey));  // 서명 알고리즘과 비밀 키로 서명
    }

    /**
     * JWT 토큰에서 로그인 ID를 추출하는 메서드
     * @param token JWT 토큰
     * @return JWT에서 추출한 로그인 ID
     */
    public String getLoginIdFromToken(String token) {
        // 토큰을 파싱하여 로그인 ID를 추출
        DecodedJWT decodedJWT = decodeToken(token);  // JWT 파싱
        return decodedJWT.getSubject();  // 토큰의 subject (로그인 ID)를 반환
    }

    /**
     * JWT 토큰을 검증하는 메서드
     * @param token JWT 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // JWTVerifier 객체를 사용해 토큰의 유효성을 검증
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey))  // 서명에 사용된 알고리즘과 키를 지정
                    .build();
            verifier.verify(token);  // 토큰 검증

            // 토큰이 화이트리스트에 있는지 Redis에서 확인
            if (!redisService.isTokenInWhitelist(token)) {
                return false;  // 화이트리스트에 없으면 유효하지 않음
            }

            return true;  // 검증 성공 시 true 반환
        } catch (JWTVerificationException exception) {
            return false;  // 검증 실패 시 false 반환
        }
    }

    /**
     * JWT 토큰을 디코딩하는 메서드
     * @param token JWT 토큰
     * @return DecodedJWT 객체 (파싱된 JWT)
     */
    private DecodedJWT decodeToken(String token) {
        return JWT.require(Algorithm.HMAC512(secretKey))  // 서명에 사용된 알고리즘과 키를 지정
                .build()
                .verify(token);  // 토큰을 검증하고 디코딩
    }
}

