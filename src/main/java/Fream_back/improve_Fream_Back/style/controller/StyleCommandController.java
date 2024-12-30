package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.service.StyleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/styles/commands")
@RequiredArgsConstructor
public class StyleCommandController {

    private final StyleCommandService styleCommandService;

    // 스타일 생성
    @PostMapping
    public ResponseEntity<Long> createStyle(
            @RequestParam String email,
            @RequestParam List<Long> orderItemIds,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> mediaFiles
    ) {
        Style createdStyle = styleCommandService.createStyle(email, orderItemIds, content, mediaFiles);
        return ResponseEntity.ok(createdStyle.getId());
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
            @RequestParam(required = false) List<MultipartFile> newMediaFiles,
            @RequestParam(required = false) List<String> existingUrlsFromFrontend
    ) {
        styleCommandService.updateStyle(styleId, content, newMediaFiles, existingUrlsFromFrontend);
        return ResponseEntity.ok().build();
    }


    // 스타일 삭제
    @DeleteMapping("/{styleId}")
    public ResponseEntity<Void> deleteStyle(@PathVariable Long styleId) {
        styleCommandService.deleteStyle(styleId);
        return ResponseEntity.ok().build();
    }
}
