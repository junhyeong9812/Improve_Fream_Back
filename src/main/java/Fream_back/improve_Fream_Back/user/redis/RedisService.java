package Fream_back.improve_Fream_Back.user.redis;

import Fream_back.improve_Fream_Back.user.entity.Gender;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Access Token + 사용자 정보를 Hash 구조로 저장
    public void addAccessToken(String accessToken, String email, Integer age, Gender gender, long expirationMillis,String ip) {
        String key = "access:" + accessToken;
        redisTemplate.opsForHash().put(key, "email", email);
        redisTemplate.opsForHash().put(key, "age", String.valueOf(age));
        redisTemplate.opsForHash().put(key, "gender", gender.toString());
        if (ip != null) {
            redisTemplate.opsForHash().put(key, "ip", ip);
        }

        // 유효 시간 설정 (밀리초 -> Duration 변환)
        redisTemplate.expire(key, Duration.ofMillis(expirationMillis));
    }
    //Refresh Token도 저장 (이때는 보통 email 정보만 저장)
    public void addRefreshToken(String refreshToken, String email, long refreshExpirationMillis) {
        String key = "refresh:" + refreshToken;
        redisTemplate.opsForHash().put(key, "email", email);
        // 만료시간 지정
        redisTemplate.expire(key, Duration.ofMillis(refreshExpirationMillis));
    }

    //Access Token 유효 여부 (Redis에 존재하는지 확인)
    public boolean isAccessTokenValid(String accessToken) {
        String key = "access:" + accessToken;
        return redisTemplate.hasKey(key);
    }
    //Refresh Token 유효 여부
    public boolean isRefreshTokenValid(String refreshToken) {
        String key = "refresh:" + refreshToken;
        return redisTemplate.hasKey(key);
    }

    //Access Token에서 email 조회
    public String getEmailByAccessToken(String accessToken) {
        String key = "access:" + accessToken;
        Object emailObj = redisTemplate.opsForHash().get(key, "email");
        return emailObj != null ? emailObj.toString() : null;
    }

    //Refresh Token에서 email 조회
    public String getEmailByRefreshToken(String refreshToken) {
        String key = "refresh:" + refreshToken;
        Object emailObj = redisTemplate.opsForHash().get(key, "email");
        return emailObj != null ? emailObj.toString() : null;
    }

    //Access Token에서 나이, 성별 조회
    public Integer getAgeByAccessToken(String accessToken) {
        String key = "access:" + accessToken;
        Object ageObj = redisTemplate.opsForHash().get(key, "age");
        if (ageObj != null) {
            return Integer.valueOf(ageObj.toString());
        }
        return null;
    }
    public Gender getGenderByAccessToken(String accessToken) {
        String key = "access:" + accessToken;
        Object genderObj = redisTemplate.opsForHash().get(key, "gender");
        if (genderObj != null) {
            return Gender.valueOf(genderObj.toString());
        }
        return null;
    }
    //Access Token, Refresh Token 삭제(로그아웃 등)
    public void removeAccessToken(String accessToken) {
        redisTemplate.delete("access:" + accessToken);
    }

    public void removeRefreshToken(String refreshToken) {
        redisTemplate.delete("refresh:" + refreshToken);
    }


    // 화이트리스트 토큰을 Redis에 저장 (유효시간 1시간)
    public void addTokenToWhitelist(String token) {
        // 유효 시간 1시간 설정
        Duration expiryDuration = Duration.ofHours(1);
        redisTemplate.opsForValue().set("whitelist:" + token, token, expiryDuration);
    }


    // 토큰이 화이트리스트에 존재하는지 확인
    public boolean isTokenInWhitelist(String token) {
        return redisTemplate.hasKey("whitelist:" + token);
    }

    // 화이트리스트에서 토큰 삭제
    public void removeTokenFromWhitelist(String token) {
        redisTemplate.delete("whitelist:" + token);
    }
    // 토큰이 화이트리스트에서 삭제되었는지 확인
    public boolean isTokenRemoved(String token) {
        return !redisTemplate.hasKey("whitelist:" + token);
    }
}
