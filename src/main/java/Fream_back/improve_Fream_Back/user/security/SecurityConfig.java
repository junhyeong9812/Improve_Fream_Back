package Fream_back.improve_Fream_Back.user.security;

import Fream_back.improve_Fream_Back.user.Jwt.JwtAuthenticationFilter;
import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.redis.RedisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisService redisService;  // RedisService를 주입받습니다.

    // JwtTokenProvider와 CustomUserDetailsService, RedisService를 생성자 주입
    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService, RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.redisService = redisService;  // RedisService를 주입받음
    }

    // AuthenticationManager를 Bean으로 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // userDetailsService 설정 및 패스워드 인코더 설정
        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    // SecurityFilterChain을 통해 보안 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("**","/api/deliveries/**").permitAll()  // 로그인과 회원가입은 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // ROLE_ADMIN만 접근 가능
                        .anyRequest().authenticated()  // 그 외 모든 요청은 인증된 사용자만 허용
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisService), UsernamePasswordAuthenticationFilter.class);  // JwtAuthenticationFilter만 추가하고, 필터 순서 지정
        return http.build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // 허용할 도메인 설정
//        configuration.setAllowedOrigins(List.of("http://localhost**"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);  // 자격증명 허용 (예: 쿠키)
//
//        // 모든 경로에 대해 CORS 정책 적용
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    // PasswordEncoder 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt 패스워드 암호화
    }
}