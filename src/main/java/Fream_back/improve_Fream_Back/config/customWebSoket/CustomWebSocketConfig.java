//package Fream_back.improve_Fream_Back.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//public class CustomWebSocketConfig implements WebSocketConfigurer {
//
//    private final CustomWebSocketHandler customWebSocketHandler;
//
//    public CustomWebSocketConfig(CustomWebSocketHandler customWebSocketHandler) {
//        this.customWebSocketHandler = customWebSocketHandler;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(customWebSocketHandler, "/ws/custom")
//                .setAllowedOrigins("*"); // 커스텀 WebSocket 핸들러 엔드포인트
//    }
//}
