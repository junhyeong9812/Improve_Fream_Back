package Fream_back.improve_Fream_Back.user.Jwt;

import Fream_back.improve_Fream_Back.user.entity.Gender;
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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        String accessToken = extractAccessToken(request);

        // JWT 서명 + 만료시간 검증
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            // Redis 화이트리스트 (또는 accessTokenValid) 검사
            if (redisService.isAccessTokenValid(accessToken)) {
                // 토큰에서 이메일 추출
                String email = jwtTokenProvider.getEmailFromToken(accessToken);
                // Redis에서 추가 정보(나이, 성별) 가져오기
                Integer age = redisService.getAgeByAccessToken(accessToken);
                Gender gender = redisService.getGenderByAccessToken(accessToken);
                // 스프링 시큐리티 인증 객체 구성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, null);

                // 여기서 UserInfo를 생성해서 details로 넣어줌
                UserInfo userInfo = new UserInfo(age, gender);
                authentication.setDetails(userInfo);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        } else {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }



    // === 토큰 추출 (헤더 기준) ===
    private String extractAccessToken(HttpServletRequest request) {
        // 예: "Authorization: Bearer xxxxx..."
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String extractRefreshToken(HttpServletRequest request) {
        // 예: "RefreshToken: Bearer xxxxx..."
        String header = request.getHeader("RefreshToken");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * 나이, 성별을 담는 임시 클래스(또는 Principal 커스텀 클래스로 대체 가능)
     */
    public static class UserInfo {
        private final Integer age;
        private final Gender gender;
        public UserInfo(Integer age, Gender gender) {
            this.age = age;
            this.gender = gender;
        }
        public Integer getAge() { return age; }
        public Gender getGender() { return gender; }
    }
}