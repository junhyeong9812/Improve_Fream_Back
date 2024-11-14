package Fream_back.improve_Fream_Back.user.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
}
