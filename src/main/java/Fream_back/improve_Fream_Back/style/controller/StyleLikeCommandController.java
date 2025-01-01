package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.service.StyleLikeCommandService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/styles/likes/commands")
@RequiredArgsConstructor
public class StyleLikeCommandController {

    private final StyleLikeCommandService styleLikeCommandService;

    // 스타일 좋아요 토글
    @PostMapping("/{styleId}/toggle")
    public ResponseEntity<Void> toggleLike(
            @PathVariable("styleId") Long styleId
    ) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 컨텍스트에서 이메일 추출
        styleLikeCommandService.toggleLike(email, styleId);
        return ResponseEntity.ok().build();
    }
}
