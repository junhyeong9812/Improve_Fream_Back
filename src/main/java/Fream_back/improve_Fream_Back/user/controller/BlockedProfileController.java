package Fream_back.improve_Fream_Back.user.controller;

import Fream_back.improve_Fream_Back.user.dto.BlockedProfileDto;
import Fream_back.improve_Fream_Back.user.service.BlockProfile.BlockedProfileCommandService;
import Fream_back.improve_Fream_Back.user.service.BlockProfile.BlockedProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<String> blockProfile(@RequestParam Long blockedProfileId) {
        String email = extractEmailFromSecurityContext();
        blockedProfileCommandService.blockProfile(email, blockedProfileId);
        return ResponseEntity.ok("프로필 차단이 완료되었습니다.");
    }

    // 프로필 차단 해제
    @DeleteMapping
    public ResponseEntity<String> unblockProfile(@RequestParam Long blockedProfileId) {
        String email = extractEmailFromSecurityContext();
        blockedProfileCommandService.unblockProfile(email, blockedProfileId);
        return ResponseEntity.ok("프로필 차단이 해제되었습니다.");
    }

    // 차단된 프로필 목록 조회
    @GetMapping
    public ResponseEntity<List<BlockedProfileDto>> getBlockedProfiles() {
        String email = extractEmailFromSecurityContext();
        List<BlockedProfileDto> blockedProfiles = blockedProfileQueryService.getBlockedProfiles(email);
        return ResponseEntity.ok(blockedProfiles);
    }

    // SecurityContextHolder에서 이메일 추출
    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다."); // 인증 실패 처리
    }
}