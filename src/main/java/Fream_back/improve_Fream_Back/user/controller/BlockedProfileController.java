package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.dto.BlockedProfileDto;
import Fream_back.improve_Fream_Back.user.service.BlockProfile.BlockedProfileCommandService;
import Fream_back.improve_Fream_Back.user.service.BlockProfile.BlockedProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/blocked")
@RequiredArgsConstructor
public class BlockedProfileController {

    private final BlockedProfileCommandService blockedProfileCommandService;
    private final BlockedProfileQueryService blockedProfileQueryService;

    // 프로필 차단
    @PostMapping
    public ResponseEntity<String> blockProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Long blockedProfileId) {
        String email = extractEmailFromToken(authorizationHeader);
        blockedProfileCommandService.blockProfile(email, blockedProfileId);
        return ResponseEntity.ok("프로필 차단이 완료되었습니다.");
    }

    // 프로필 차단 해제
    @DeleteMapping
    public ResponseEntity<String> unblockProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Long blockedProfileId) {
        String email = extractEmailFromToken(authorizationHeader);
        blockedProfileCommandService.unblockProfile(email, blockedProfileId);
        return ResponseEntity.ok("프로필 차단이 해제되었습니다.");
    }

    // 차단된 프로필 목록 조회
    @GetMapping
    public ResponseEntity<List<BlockedProfileDto>> getBlockedProfiles(
            @RequestHeader("Authorization") String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        List<BlockedProfileDto> blockedProfiles = blockedProfileQueryService.getBlockedProfiles(email);
        return ResponseEntity.ok(blockedProfiles);
    }

    // JWT 토큰에서 이메일 추출 (공통 유틸로 분리 가능)
    private String extractEmailFromToken(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }
}
