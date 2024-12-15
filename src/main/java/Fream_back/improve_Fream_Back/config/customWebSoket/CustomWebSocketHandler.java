//package Fream_back.improve_Fream_Back.config;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class CustomWebSocketHandler extends TextWebSocketHandler {
//
//    private final RedisTemplate<String, String> redisTemplate;
//
//    public CustomWebSocketHandler(RedisTemplate<String, String> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        Long userId = (Long) session.getAttributes().get("userId");
//        if (userId != null) {
//            String redisKey = "WebSocket:User:" + userId;
//
//            // Redis에서 사용자 상태 삭제
//            redisTemplate.delete(redisKey);
//            System.out.println("WebSocket 연결 종료: 사용자 ID = " + userId);
//        }
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        if ("PING".equals(payload)) { // 클라이언트가 PING 메시지 전송
//            Long userId = (Long) session.getAttributes().get("userId");
//            if (userId != null) {
//                String redisKey = "WebSocket:User:" + userId;
//
//                // TTL 갱신 (30분으로 재설정)
//                redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
//                System.out.println("WebSocket TTL 갱신: 사용자 ID = " + userId);
//            }
//        }
//    }
//}
//
