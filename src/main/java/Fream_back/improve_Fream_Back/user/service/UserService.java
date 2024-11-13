package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.dto.*;
import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UserService
 *
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 로그인, 아이디 찾기, 비밀번호 재설정 및 업데이트 기능을 제공합니다.
 */

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 로그인 서비스
     * 주어진 loginId와 비밀번호를 기반으로 사용자 조회.
     *
     * @param loginDto 로그인에 필요한 로그인 아이디 및 비밀번호를 포함한 DTO
     * @return Optional<User> 로그인에 성공하면 해당 사용자 정보, 실패 시 빈 Optional
     */
    public Optional<User> login(LoginDto loginDto) {
        return userRepository.findByLoginIdAndPassword(
                loginDto.getLoginId(), loginDto.getPassword());
    }

    /**
     * 연락처(전화번호)를 통한 아이디 찾기 서비스
     * 사용자의 전화번호를 기준으로 loginId를 조회.
     *
     * @param recoveryDto 전화번호를 포함한 LoginIdRecoveryDto
     * @return Optional<String> 사용자의 loginId가 존재하면 해당 값 반환, 없으면 빈 Optional
     */
    public Optional<String> findLoginIdByPhoneNumber(LoginIdRecoveryDto recoveryDto) {
        return userRepository.findByPhoneNumber(recoveryDto.getPhoneNumber())
                .map(User::getLoginId);
    }

    /**
     * 이메일 인증을 통한 아이디 찾기 서비스
     * 사용자의 이메일을 기준으로 loginId를 조회.
     *
     * @param recoveryDto 이메일을 포함한 LoginIdRecoveryDto
     * @return Optional<String> 사용자의 loginId가 존재하면 해당 값 반환, 없으면 빈 Optional
     */
    public Optional<String> findLoginIdByEmail(LoginIdRecoveryDto recoveryDto) {
        return userRepository.findByEmail(recoveryDto.getEmail())
                .map(User::getLoginId);
    }

    /**
     * 비밀번호 재설정 요청 - 사용자 정보 인증
     * 사용자가 입력한 loginId와 전화번호 또는 이메일이 일치하는지 확인.
     *
     * @param requestDto loginId, 전화번호, 이메일 정보를 포함한 비밀번호 재설정 요청 DTO
     * @return boolean 사용자 정보가 일치하면 true, 아니면 false
     */
    public boolean validateUserForPasswordReset(PasswordResetRequestDto requestDto) {
        return userRepository.findByLoginIdAndPhoneNumberOrEmail(
                requestDto.getLoginId(), requestDto.getPhoneNumber(), requestDto.getEmail()
        ).isPresent();
    }

    /**
     * 비밀번호 업데이트 서비스
     * 인증된 사용자에 대해 비밀번호를 업데이트.
     *
     * @param updateDto loginId와 새 비밀번호를 포함한 DTO
     * @return boolean 비밀번호 업데이트에 성공하면 true, 사용자 찾기 실패 시 false
     */
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

    /**
     * loginId 중복 여부 확인
     *
     * @param loginId 확인할 로그인 아이디
     * @return 중복 여부 ("ok" 또는 "duplicate")
     */
    public String checkDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            return "duplicate";
        }
        return "ok";
    }

    /**
     * 새로운 사용자 등록
     *
     * @param dto UserSignupDto 사용자 등록에 필요한 데이터
     * @return 등록된 User 엔티티
     */
    public User registerUser(UserSignupDto dto) {
        User newUser = User.builder()
                .loginId(dto.getLoginId())
                .password(dto.getPassword())
                .nickname(dto.getNickname())
                .realName(dto.getRealName())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .phoneNotificationConsent(dto.getPhoneNotificationConsent())
                .emailNotificationConsent(dto.getEmailNotificationConsent())
                .role(Role.USER)  // DTO에서 받은 기본값 Role.USER 사용
                .build();

        return userRepository.save(newUser);
    }
}