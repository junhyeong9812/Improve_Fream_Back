package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.service.StyleCommentLikeCommandService;
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
            @RequestParam String email,
            @PathVariable Long commentId
    ) {
        styleCommentLikeCommandService.toggleCommentLike(email, commentId);
        return ResponseEntity.ok().build();
    }
}
