package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.service.StyleInterestCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/styles/interests/commands")
@RequiredArgsConstructor
public class StyleInterestCommandController {

    private final StyleInterestCommandService styleInterestCommandService;

    // 스타일 관심 토글
    @PostMapping("/{styleId}/toggle")
    public ResponseEntity<Void> toggleInterest(
            @RequestParam String email,
            @PathVariable Long styleId
    ) {
        styleInterestCommandService.toggleStyleInterest(email, styleId);
        return ResponseEntity.ok().build();
    }
}
