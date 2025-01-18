package Fream_back.improve_Fream_Back.user.Jwt;

import Fream_back.improve_Fream_Back.user.entity.Gender;
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
    private long accessTokenValidityMs; // 액세스 토큰 만료 시간 (밀리초)

    @Value("${jwt.refreshExpiration}")
    private long refreshTokenValidityMs; // 리프레시 토큰 만료 시간 (밀리초)

    private final RedisService redisService;

    // RedisService 주입
    public JwtTokenProvider(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * AccessToken + RefreshToken 동시 발급
     * (email, age, gender를 함께 받아서 Access Token 생성 시 저장)
     */
    public TokenDto generateTokenPair(String email, Integer age, Gender gender,String ip) {
        long now = System.currentTimeMillis();

        // === Access Token 생성 ===
        Date accessExpiry = new Date(now + accessTokenValidityMs);
        String accessToken = JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(accessExpiry)
                .sign(Algorithm.HMAC512(secretKey));

        // === Refresh Token 생성 ===
        Date refreshExpiry = new Date(now + refreshTokenValidityMs);
        String refreshToken = JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(refreshExpiry)
                .sign(Algorithm.HMAC512(secretKey));

        // === Redis에 저장 (화이트리스트) ===
        // 액세스 토큰은 나이/성별 같이 저장, 리프레시 토큰은 email만 저장
        redisService.addAccessToken(accessToken, email, age, gender, accessTokenValidityMs,ip);
        redisService.addRefreshToken(refreshToken, email, refreshTokenValidityMs);

        return new TokenDto(accessToken, refreshToken);
    }
    /**
     * JWT 토큰 검증 (서명, 만료시간)
     */
    public boolean validateToken(String token) {
        try {
            DecodedJWT decoded = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);
            return true; // 서명/만료시간 유효
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰에서 이메일(subject) 추출
     */
    public String getEmailFromToken(String token) {
        DecodedJWT decoded = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
        return decoded.getSubject();
    }

    /**
     * JWT 토큰을 생성하는 메서드
     * @param email JWT에 포함할 이메일
     * @return 생성된 JWT 토큰
     */
//    public String generateToken(String email) {
//        // JWT 토큰을 생성하여 반환
//        return JWT.create()
//                .withSubject(email)  // 이메일을 JWT의 subject로 설정
//                .withIssuedAt(new Date())  // 발급 시간을 현재 시간으로 설정
//                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))  // 만료 시간을 설정
//                .sign(Algorithm.HMAC512(secretKey));  // 서명 알고리즘과 비밀 키로 서명
//    }

    /**
     * JWT 토큰에서 이메일을 추출하는 메서드
     * @param token JWT 토큰
     * @return JWT에서 추출한 이메일
     */
//    public String getEmailFromToken(String token) {
//        // 토큰을 파싱하여 이메일을 추출
//        DecodedJWT decodedJWT = decodeToken(token);  // JWT 파싱
//        return decodedJWT.getSubject();  // 토큰의 subject (이메일)를 반환
//    }

    /**
     * JWT 토큰을 검증하는 메서드
     * @param token JWT 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
//    public boolean validateToken(String token) {
//        try {
//            // JWTVerifier 객체를 사용해 토큰의 유효성을 검증
//            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey))  // 서명에 사용된 알고리즘과 키를 지정
//                    .build();
//            verifier.verify(token);  // 토큰 검증
//
//            // 토큰이 화이트리스트에 있는지 Redis에서 확인
//            if (!redisService.isTokenInWhitelist(token)) {
//                return false;  // 화이트리스트에 없으면 유효하지 않음
//            }
//
//            return true;  // 검증 성공 시 true 반환
//        } catch (JWTVerificationException exception) {
//            return false;  // 검증 실패 시 false 반환
//        }
//    }

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
