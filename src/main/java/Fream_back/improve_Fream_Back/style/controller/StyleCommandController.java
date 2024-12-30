package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.service.StyleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/styles/commands")
@RequiredArgsConstructor
public class StyleCommandController {

    private final StyleCommandService styleCommandService;

    // 스타일 생성
    @PostMapping
    public ResponseEntity<Style> createStyle(
            @RequestParam String email,
            @RequestParam Long orderItemId,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile mediaFile
    ) {
        Style createdStyle = styleCommandService.createStyle(email, orderItemId, content, mediaFile);
        return ResponseEntity.ok(createdStyle);
    }

    // 뷰 카운트 증가
    @PostMapping("/{styleId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long styleId) {
        styleCommandService.incrementViewCount(styleId);
        return ResponseEntity.ok().build();
    }

    // 스타일 업데이트
    @PutMapping("/{styleId}")
    public ResponseEntity<Void> updateStyle(
            @PathVariable Long styleId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile mediaFile
    ) {
        styleCommandService.updateStyle(styleId, content, mediaFile);
        return ResponseEntity.ok().build();
    }

    // 스타일 삭제
    @DeleteMapping("/{styleId}")
    public ResponseEntity<Void> deleteStyle(@PathVariable Long styleId) {
        styleCommandService.deleteStyle(styleId);
        return ResponseEntity.ok().build();
    }
}
