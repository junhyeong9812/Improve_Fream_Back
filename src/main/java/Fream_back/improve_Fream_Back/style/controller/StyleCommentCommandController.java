package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.entity.StyleComment;
import Fream_back.improve_Fream_Back.style.service.StyleCommentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/styles/comments/commands")
@RequiredArgsConstructor
public class StyleCommentCommandController {

    private final StyleCommentCommandService styleCommentCommandService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<StyleComment> addComment(
            @RequestParam String email,
            @RequestParam Long styleId,
            @RequestParam String content,
            @RequestParam(required = false) Long parentCommentId
    ) {
        StyleComment comment = styleCommentCommandService.addComment(email, styleId, content, parentCommentId);
        return ResponseEntity.ok(comment);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestParam String updatedContent
    ) {
        styleCommentCommandService.updateComment(commentId, updatedContent);
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        styleCommentCommandService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
