package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.Jwt.TokenDto;
import Fream_back.improve_Fream_Back.user.dto.LoginRequestDto;
import Fream_back.improve_Fream_Back.user.entity.Gender;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.redis.RedisService;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    //로그인 로직
    @Transactional
    public TokenDto login(LoginRequestDto loginRequestDto,String ip) {
        // 사용자 조회
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 나이, 성별 (User 엔티티에 있다면)
        Integer age = user.getAge();
        Gender gender = user.getGender();

        // JWT (AccessToken + RefreshToken) 발급
        TokenDto tokenDto = jwtTokenProvider.generateTokenPair(user.getEmail(), age, gender,ip);

        // tokenDto 내부에는 accessToken, refreshToken 둘 다 있음
        return tokenDto;
    }
}
