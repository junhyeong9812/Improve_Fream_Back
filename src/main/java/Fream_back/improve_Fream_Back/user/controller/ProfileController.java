package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.dto.ProfileInfoDto;
import Fream_back.improve_Fream_Back.user.dto.ProfileUpdateDto;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileCommandService;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileQueryService;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileQueryService profileQueryService;
    private final ProfileCommandService profileCommandService;
    private final FileUtils fileUtils;

    // SecurityContextHolder에서 이메일 추출
    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다."); // 인증 실패 처리
    }

    @GetMapping
    public ResponseEntity<ProfileInfoDto> getProfile() {
        String email = extractEmailFromSecurityContext();
        ProfileInfoDto profileInfo = profileQueryService.getProfileInfo(email);
        return ResponseEntity.ok(profileInfo);
    }

    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<String> updateProfile(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "dto") ProfileUpdateDto dto) {
        System.out.println("Content-Type: " + profileImage.getContentType());
        System.out.println("File Name: " + profileImage.getOriginalFilename());
        System.out.println("DTO: " + dto);
        String email = extractEmailFromSecurityContext();
        profileCommandService.updateProfile(email, dto, profileImage);
        return ResponseEntity.ok("프로필이 성공적으로 업데이트되었습니다.");
    }

    // 프로필 이미지 파일 제공
    @GetMapping("/{profileId}/image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable("profileId") Long profileId) throws IOException {
        // 프로필에서 이미지 파일명 조회
        String profileImageFileName = profileQueryService.getProfileImageFileName(profileId);
        String directory = "profile_images"; // 이미지 저장 디렉토리

        // 파일 읽기
        File imageFile = new File(directory + File.separator + profileImageFileName);
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("이미지 파일이 존재하지 않습니다.");
        }

        byte[] imageBytes = Files.readAllBytes(Paths.get(imageFile.getPath()));

        // 응답 생성 (Content-Type 설정)
        return ResponseEntity.ok()
                .header("Content-Type", Files.probeContentType(imageFile.toPath()))
                .body(imageBytes);
    }
}
