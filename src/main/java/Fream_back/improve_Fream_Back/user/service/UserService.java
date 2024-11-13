package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 로그인 서비스 - loginId와 비밀번호로 사용자 조회
    public Optional<User> login(LoginDto loginDto) {
        return userRepository.findByLoginIdAndPassword(
                loginDto.getLoginId(), loginDto.getPassword());
    }

    // 연락처(전화번호)를 통한 아이디 찾기 서비스
    public Optional<String> findLoginIdByPhoneNumber(LoginIdRecoveryDto recoveryDto) {
        return userRepository.findByPhoneNumber(recoveryDto.getPhoneNumber())
                .map(User::getLoginId);
    }

    // 이메일 인증을 통한 아이디 찾기 서비스
    public Optional<String> findLoginIdByEmail(LoginIdRecoveryDto recoveryDto) {
        return userRepository.findByEmail(recoveryDto.getEmail())
                .map(User::getLoginId);
    }

    // 비밀번호 재설정 요청 - 사용자 정보 인증 (전화번호 또는 이메일 일치 확인)
    public boolean validateUserForPasswordReset(PasswordResetRequestDto requestDto) {
        return userRepository.findByLoginIdAndPhoneNumberOrEmail(
                requestDto.getLoginId(), requestDto.getPhoneNumber(), requestDto.getEmail()
        ).isPresent();
    }

    // 비밀번호 업데이트 서비스 - 인증 후 비밀번호 업데이트
    @Transactional
    public boolean updatePassword(PasswordUpdateDto updateDto) {
        Optional<User> userOpt = userRepository.findByLoginId(updateDto.getLoginId());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.updatePassword(updateDto.getNewPassword()); // 더티체킹을 통한 비밀번호 업데이트
            return true;
        }

        return false;
    }
}