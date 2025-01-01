package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.service.StyleInterestCommandService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
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
            @PathVariable("styleId") Long styleId
    ) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 컨텍스트에서 이메일 추출
        styleInterestCommandService.toggleStyleInterest(email, styleId);
        return ResponseEntity.ok().build();
    }
}
