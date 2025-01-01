package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.dto.UpdateCommentRequestDto;
import Fream_back.improve_Fream_Back.style.entity.StyleComment;
import Fream_back.improve_Fream_Back.style.service.StyleCommentCommandService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
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
            @RequestParam("styleId") Long styleId,
            @RequestParam("content") String content,
            @RequestParam(value = "parentCommentId", required = false) Long parentCommentId
    ) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 컨텍스트에서 이메일 추출
        StyleComment comment = styleCommentCommandService.addComment(email, styleId, content, parentCommentId);
        return ResponseEntity.ok(comment);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody UpdateCommentRequestDto updateCommentRequestDto
    ) {
        styleCommentCommandService.updateComment(commentId,  updateCommentRequestDto.getUpdatedContent());
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("commentId") Long commentId) {
        styleCommentCommandService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
