//package Fream_back.improve_Fream_Back.user.Jwt;
//
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.auth0.jwt.interfaces.JWTVerifier;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtUtils {
//
//    private final JwtConfig jwtConfig;
//
//    public JwtUtils(JwtConfig jwtConfig) {
//        this.jwtConfig = jwtConfig;
//    }
//
//    /**
//     * JWT 토큰을 생성하는 메서드
//     * @param loginId JWT에 포함할 로그인 ID
//     * @return 생성된 JWT 토큰
//     */
//    public String generateToken(String loginId) {
//        return JWT.create()
//                .withSubject(loginId)  // 로그인 ID를 JWT의 subject로 설정
//                .withIssuedAt(new Date())  // 발급 시간을 현재 시간으로 설정
//                .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpirationTime()))  // 만료 시간을 설정
//                .sign(Algorithm.HMAC512(jwtConfig.getSecretKey()));  // 서명 알고리즘과 비밀 키로 서명
//    }
//
//    /**
//     * JWT 토큰에서 로그인 ID를 추출하는 메서드
//     * @param token JWT 토큰
//     * @return JWT에서 추출한 로그인 ID
//     */
//    public String getLoginIdFromToken(String token) {
//        DecodedJWT decodedJWT = decodeToken(token);  // JWT 파싱
//        return decodedJWT.getSubject();  // 토큰의 subject (로그인 ID)를 반환
//    }
//
//    /**
//     * JWT 토큰을 검증하는 메서드
//     * @param token JWT 토큰
//     * @return 토큰이 유효하면 true, 아니면 false
//     */
//    public boolean validateToken(String token) {
//        try {
//            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtConfig.getSecretKey()))  // 서명에 사용된 알고리즘과 키를 지정
//                    .build();
//            verifier.verify(token);  // 토큰 검증
//            return true;
//        } catch (JWTVerificationException exception) {
//            return false;
//        }
//    }
//
//    /**
//     * JWT 토큰을 디코딩하는 메서드
//     * @param token JWT 토큰
//     * @return DecodedJWT 객체 (파싱된 JWT)
//     */
//    private DecodedJWT decodeToken(String token) {
//        return JWT.require(Algorithm.HMAC512(jwtConfig.getSecretKey()))  // 서명에 사용된 알고리즘과 키를 지정
//                .build()
//                .verify(token);  // 토큰을 검증하고 디코딩
//    }
//}