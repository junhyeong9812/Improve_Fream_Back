package Fream_back.improve_Fream_Back.user.Jwt;

import Fream_back.improve_Fream_Back.user.redis.RedisService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    // 생성자: JwtTokenProvider를 주입받아 필터 초기화
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰 검증 후 Redis 화이트리스트에 존재하는지 확인
            if (redisService.isTokenInWhitelist(token)) {
                String loginId = jwtTokenProvider.getLoginIdFromToken(token);
                // 인증 객체에 권한 정보 추가 (예: roles, authorities)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginId, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰이 화이트리스트에 없으면 인증 실패 (401 상태 코드)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is not in the whitelist");
                return;
            }
        } else {
            // 토큰 검증 실패 시 인증 실패 (401 상태 코드)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
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
