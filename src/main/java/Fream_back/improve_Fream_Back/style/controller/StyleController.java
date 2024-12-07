package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleSearchDto;
import Fream_back.improve_Fream_Back.style.dto.StyleUpdateDto;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.service.StyleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/styles")
public class StyleController {

    private final StyleService styleService;

    public StyleController(StyleService styleService) {
        this.styleService = styleService;
    }

    // 임시 저장 엔드포인트
    @PostMapping("/upload-temp")
    public ResponseEntity<?> uploadTempFile(@RequestParam MultipartFile file) {
        try {
            String tempFilePath = styleService.saveTemporaryFile(file);
            return ResponseEntity.ok(tempFilePath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createStyle(@RequestParam Long userId,
                                         @RequestParam Long orderItemId,
                                         @RequestParam String content,
                                         @RequestParam(required = false) Integer rating,
                                         @RequestParam MultipartFile file) {
        try {
            Long styleId = styleService.createStyle(userId, orderItemId, content, rating, file);
            return ResponseEntity.ok(styleId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{styleId}/update")
    public ResponseEntity<?> updateStyle(@PathVariable Long styleId,
                                         @RequestBody StyleUpdateDto updateDto) {
        try {
            Long updatedStyleId = styleService.updateStyle(styleId, updateDto);
            return ResponseEntity.ok(updatedStyleId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{styleId}/delete")
    public ResponseEntity<?> deleteStyle(@PathVariable Long styleId,
                                         @RequestParam Long userId) {
        try {
            styleService.deleteStyle(styleId, userId);
            return ResponseEntity.ok("스타일이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/search")
    public Page<StyleResponseDto> searchStyles(
            @RequestBody StyleSearchDto searchDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        return styleService.getPagedStyles(searchDto, pageable);
    }
    @GetMapping("/{styleId}")
    public ResponseEntity<?> getStyleById(@PathVariable Long styleId) {
        try {
            StyleResponseDto style = styleService.getStyleById(styleId);
            return ResponseEntity.ok(style);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
