package Fream_back.improve_Fream_Back.style;

import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.service.StyleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/styles")
public class StyleController {

    private final StyleService styleService;

    public StyleController(StyleService styleService) {
        this.styleService = styleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createStyle(@RequestParam Long userId,
                                         @RequestParam Long orderItemId,
                                         @RequestParam String content,
                                         @RequestParam(required = false) Integer rating,
                                         @RequestParam MultipartFile file) {
        try {
            Style style = styleService.createStyle(userId, orderItemId, content, rating, file);
            return ResponseEntity.ok(style);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
