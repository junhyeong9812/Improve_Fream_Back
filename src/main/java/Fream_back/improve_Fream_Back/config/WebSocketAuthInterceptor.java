package Fream_back.improve_Fream_Back.config;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public WebSocketAuthInterceptor(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        try {
            String token = ((ServletServerHttpRequest) request).getServletRequest().getParameter("token");
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }

            String email = jwtTokenProvider.getEmailFromToken(token);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            attributes.put("userId", user.getId());

            logger.info("WebSocket 핸드셰이크 성공: 사용자 ID = {}", user.getId());
            return true;
        } catch (Exception e) {
            logger.error("WebSocket 핸드셰이크 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {

        if (exception != null) {
            String uri = request.getURI().toString();
            String ip = request.getRemoteAddress() != null ? request.getRemoteAddress().toString() : "알 수 없음";
            logger.error("WebSocket 연결 실패: 요청 경로 = {}, 요청 IP = {}, 실패 이유 = {}", uri, ip, exception.getMessage());
            return;
        }

        Object attributeObject = ((ServletServerHttpRequest) request).getServletRequest().getAttribute("attributes");
        if (attributeObject instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> attributes = (Map<String, Object>) attributeObject;

            if (attributes.containsKey("userId")) {
                Long userId = (Long) attributes.get("userId");

                String redisKey = "WebSocket:User:" + userId;
                redisTemplate.opsForValue().set(redisKey, "CONNECTED");
                redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);

                logger.info("WebSocket 연결 성공: 사용자 ID = {}", userId);
            } else {
                logger.warn("WebSocket 연결 성공했으나 'userId'가 attributes에 없음.");
            }
        } else {
            logger.error("WebSocket 연결 성공했으나 attributes가 Map 타입이 아님.");
        }
    }
}

