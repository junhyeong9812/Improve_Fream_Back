package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.Jwt.TokenDto;
import Fream_back.improve_Fream_Back.user.dto.LoginInfoUpdateDto;
import Fream_back.improve_Fream_Back.user.entity.ShoeSize;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.redis.RedisService;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void updateLoginInfo(String email, LoginInfoUpdateDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 변경 로직
        if (dto.getPassword() != null && dto.getNewPassword() != null) {
            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            // 새 비밀번호 설정
            user.updateLoginInfo(null, passwordEncoder.encode(dto.getNewPassword()), null, null, null, null, null, null);
        }

        // ShoeSize 변환
        ShoeSize newShoeSize = dto.getNewShoeSize() != null ? ShoeSize.valueOf(dto.getNewShoeSize()) : null;

        // 업데이트 호출
        user.updateLoginInfo(dto.getNewEmail(),
                dto.getNewPassword() != null ? passwordEncoder.encode(dto.getNewPassword()) : null,
                dto.getNewPhoneNumber(),
                newShoeSize,
                dto.getAdConsent(),
                dto.getPrivacyConsent(),
                dto.getSmsConsent(),
                dto.getEmailConsent());
    }
    @Transactional
    public TokenDto reissueTokenAfterEmailChange(
            String oldAccessToken,
            String oldRefreshToken,
            String oldEmail,
            String newEmail,
            String ip
    ) {
        // 1) newEmail 로 DB에서 사용자 찾기
        User newUser = userRepository.findByEmail(newEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다: " + newEmail));

        // 2) Redis에서 old 토큰들 제거
        if (oldAccessToken != null) {
            redisService.removeAccessToken(oldAccessToken);
        }
        if (oldRefreshToken != null) {
            redisService.removeRefreshToken(oldRefreshToken);
        }

        // 3) 새 이메일 + User 정보로 토큰 재발급
        //    User에 age, gender 필드가 있다고 가정
        return jwtTokenProvider.generateTokenPair(
                newUser.getEmail(),
                newUser.getAge(),
                newUser.getGender(),
                ip
        );
    }

}
