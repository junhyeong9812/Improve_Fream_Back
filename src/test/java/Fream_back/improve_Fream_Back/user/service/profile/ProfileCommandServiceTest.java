package Fream_back.improve_Fream_Back.user.service.profile;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.dto.ProfileUpdateDto;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.ProfileRepository;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestConfig.class)
class ProfileCommandServiceTest {

    @Autowired
    private ProfileCommandService profileCommandService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private User user1;

    @Autowired
    private FileUtils fileUtils; // 실제 FileUtils 인스턴스를 사용

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Test
    @Transactional
    @DisplayName("디폴트 프로필 생성 - 성공")
    void testCreateDefaultProfileSuccess() {
        profileCommandService.createDefaultProfile(user1);
        // Then
        Profile profile = profileRepository.findByUser_Email(user1.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("디폴트 프로필 생성 실패"));

        assertThat(profile).isNotNull();
        assertThat(profile.getUser().getEmail()).isEqualTo(user1.getEmail());
        assertThat(profile.getProfileName()).isNotEmpty(); // 랜덤 생성 확인
        assertThat(profile.getName()).isEqualTo(user1.getEmail().split("@")[0]); // 이메일 앞부분 확인
        assertThat(profile.getProfileImageUrl()).isEqualTo("user.jpg"); // 기본 이미지 확인
        assertThat(profile.isPublic()).isTrue(); // 기본 공개 여부 확인
        assertThat(profile.getBio()).isEmpty(); // 기본 소개글 확인
    }

    @Test
    @Transactional
    @DisplayName("프로필 업데이트 - 성공 (이미지 포함)")
    void testUpdateProfileWithImage() throws Exception {
        // Given
        profileCommandService.createDefaultProfile(user1); // 디폴트 프로필 생성
        String newProfileName = "updatedProfile";
        String newBio = "Updated bio";
        Boolean newIsPublic = false;

        ProfileUpdateDto dto = new ProfileUpdateDto(newProfileName, "Updated Real Name", newBio, newIsPublic);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "testImage.jpg",
                "image/jpeg",
                "mock image content".getBytes()
        );

        // When
        profileCommandService.updateProfile(user1.getEmail(), dto, mockFile);

        // 영속성 컨텍스트 초기화 후 다시 조회
        entityManager.flush();
        entityManager.clear();

        Profile updatedProfile = profileRepository.findByUser_Email(user1.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));

        // Then
        System.out.println("Updated Profile Image URL: " + updatedProfile.getProfileImageUrl());

        // 파일 이름만 검증
        assertThat(updatedProfile.getProfileName()).isEqualTo(newProfileName);
        assertThat(updatedProfile.getBio()).isEqualTo(newBio);
        assertThat(updatedProfile.isPublic()).isEqualTo(newIsPublic);

        // 파일 이름만 확인 (UUID 기반으로 저장된 파일명)
        assertThat(updatedProfile.getProfileImageUrl()).startsWith("profile_");
        assertThat(updatedProfile.getProfileImageUrl()).endsWith(".jpg");
    }

    @Test
    @DisplayName("프로필 업데이트 - 실패 (존재하지 않는 이메일)")
    void testUpdateProfileWithInvalidEmail() {
        // Given
        profileCommandService.createDefaultProfile(user1);
        ProfileUpdateDto dto = new ProfileUpdateDto("newProfile", "New Real Name", "New bio", true);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "testImage.jpg",
                "image/jpeg",
                "mock image content".getBytes()
        );

        // When & Then
        assertThatThrownBy(() -> profileCommandService.updateProfile("invalid@example.com", dto, mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로필을 찾을 수 없습니다.");
    }
}
