package Fream_back.improve_Fream_Back.user.service;

import Fream_back.improve_Fream_Back.user.dto.UserRegistrationDto;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;
    private final FileUtils fileUtils;

    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        // 필수 동의 확인
        if (!dto.getIsOver14() || !dto.getTermsAgreement() || !dto.getPrivacyAgreement()) {
            throw new IllegalArgumentException("필수 동의 조건을 만족하지 않았습니다.");
        }

        // 추천인 코드가 존재할 경우 확인
        User referrer = null;
        if (dto.getReferralCode() != null) {
            referrer = userRepository.findByReferralCode(dto.getReferralCode())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 추천인 코드입니다."));
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 사용자 생성
        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .shoeSize(dto.getShoeSize())
                .referrer(referrer)
                .termsAgreement(dto.getTermsAgreement())
                .phoneNotificationConsent(dto.getAdConsent() != null ? dto.getAdConsent() : false)
                .emailNotificationConsent(dto.getOptionalPrivacyAgreement() != null ? dto.getOptionalPrivacyAgreement() : false)
                .build();

        // 개인정보 동의 설정
        user.updateConsent(dto.getAdConsent(), dto.getOptionalPrivacyAgreement());

        // 사용자 저장
        userRepository.save(user);

        // 프로필 생성 (프로필 서비스 호출)
        profileService.createDefaultProfile(user);

        return user;
    }

    @Transactional
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 프로필 이미지 삭제
        if (user.getProfile() != null && user.getProfile().getProfileImageUrl() != null) {
            fileUtils.deleteFile("profile_images", user.getProfile().getProfileImageUrl());
        }

        // 사용자 정보 삭제
        userRepository.delete(user);
    }
}
