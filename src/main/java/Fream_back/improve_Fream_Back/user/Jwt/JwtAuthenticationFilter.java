package Fream_back.improve_Fream_Back.user.Jwt;

import Fream_back.improve_Fream_Back.user.redis.RedisService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    // 생성자: JwtTokenProvider와 RedisService를 주입받아 필터 초기화
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request); // 토큰 추출

        // 토큰이 유효한지 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰 검증 후 Redis 화이트리스트에 존재하는지 확인
            if (redisService.isTokenInWhitelist(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                // 인증 객체에 이메일 설정
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 헤더에 토큰을 설정 (컨트롤러에서 사용할 수 있게)
                request.setAttribute("Authorization", "Bearer " + token);
            } else {
                // 화이트리스트에 없으면 인증 실패 (하지만 필터에서 바로 응답을 보내지 않음)
                // 헤더에 메시지 추가만 하고, 필터 체인 계속 진행
//                request.setAttribute("Authorization", "Token is not in the whitelist");
                // 화이트리스트에 없으면 인증 실패
                SecurityContextHolder.clearContext();
            }
        } else {
            // 토큰이 없거나 유효하지 않으면 인증 실패 (하지만 필터에서 바로 응답을 보내지 않음)
//            request.setAttribute("Authorization", "Invalid or expired token");
            // 토큰이 없거나 유효하지 않으면 인증 실패
            SecurityContextHolder.clearContext();
        }

        // 필터 체인에서 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출하는 메서드
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // "Bearer "를 제외한 토큰 부분 반환
        }
        return null;
    }

    @Override
    public void destroy() {
        // 필요시 필터 정리 작업
    }
}