package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.service.StyleCommentLikeCommandService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/styles/comments/likes/commands")
@RequiredArgsConstructor
public class StyleCommentLikeCommandController {

    private final StyleCommentLikeCommandService styleCommentLikeCommandService;

    // 댓글 좋아요 토글
    @PostMapping("/{commentId}/toggle")
    public ResponseEntity<Void> toggleCommentLike(
            @PathVariable("commentId") Long commentId
    ) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 컨텍스트에서 이메일 추출
        styleCommentLikeCommandService.toggleCommentLike(email, commentId);
        return ResponseEntity.ok().build();
    }
}
