package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.dto.LoginInfoUpdateDto;
import Fream_back.improve_Fream_Back.user.entity.ShoeSize;
import Fream_back.improve_Fream_Back.user.entity.User;
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
}
