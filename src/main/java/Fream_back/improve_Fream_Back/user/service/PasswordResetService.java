package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.dto.PasswordResetRequestDto;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 암호화를 위한 PasswordEncoder

    @Transactional(readOnly = true)
    public boolean checkPasswordResetEligibility(String email, String phoneNumber) {
        return userRepository.findByEmailAndPhoneNumber(email, phoneNumber).isPresent();
    }
    @Transactional
    public boolean resetPassword(PasswordResetRequestDto requestDto) {
        // 비밀번호 검증
        requestDto.validatePasswords();

        // 이메일과 전화번호로 사용자 조회
        User user = userRepository.findByEmailAndPhoneNumber(requestDto.getEmail(), requestDto.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 암호화 및 업데이트
        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.updateUser(null, encodedPassword, null, null, null, null, null);

        // 더티체크에 의해 비밀번호 자동 저장
        return true; // 정상적으로 변경된 경우 true 반환
    }
}
