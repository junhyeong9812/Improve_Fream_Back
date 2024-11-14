//package Fream_back.improve_Fream_Back.user.Jwt;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class JwtConfig {
//
//    @Value("${jwt.secret}")
//    private String secretKey;  // JWT 서명에 사용할 비밀 키
//
//    @Value("${jwt.expiration}")
//    private long expirationTime;  // JWT 만료 시간 (밀리초 단위)
//
//    public String getSecretKey() {
//        return secretKey;
//    }
//
//    public long getExpirationTime() {
//        return expirationTime;
//    }
//}
