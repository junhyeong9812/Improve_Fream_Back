package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import Fream_back.improve_Fream_Back.user.dto.ProfileInfoDto;
import Fream_back.improve_Fream_Back.user.dto.ProfileUpdateDto;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileCommandService;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileQueryService profileQueryService;
    private final ProfileCommandService profileCommandService;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 주입

    @GetMapping
    public ResponseEntity<ProfileInfoDto> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        ProfileInfoDto profileInfo = profileQueryService.getProfileInfo(email);
        return ResponseEntity.ok(profileInfo);
    }

    @PutMapping
    public ResponseEntity<String> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ProfileUpdateDto dto) {
        String email = extractEmailFromToken(authorizationHeader);
        profileCommandService.updateProfile(email, dto);
        return ResponseEntity.ok("프로필이 성공적으로 업데이트되었습니다.");
    }


    private String extractEmailFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        // JwtTokenProvider 활용하여 이메일 추출
        return jwtTokenProvider.getEmailFromToken(token);
    }
}
