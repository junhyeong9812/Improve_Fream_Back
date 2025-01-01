package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.dto.ProfileStyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleDetailResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleFilterRequestDto;
import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.service.StyleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/styles/queries")
@RequiredArgsConstructor
public class StyleQueryController {

    private final StyleQueryService styleQueryService;

    @GetMapping("/{styleId}")
    public ResponseEntity<StyleDetailResponseDto> getStyleDetail(
            @PathVariable("styleId") Long styleId) {
        StyleDetailResponseDto detail = styleQueryService.getStyleDetail(styleId);
        return ResponseEntity.ok(detail);
    }

    @GetMapping
    public ResponseEntity<Page<StyleResponseDto>> getFilteredStyles(
            @ModelAttribute StyleFilterRequestDto filterRequestDto,
            Pageable pageable
    ) {
        Page<StyleResponseDto> styles = styleQueryService.getFilteredStyles(filterRequestDto, pageable);
        return ResponseEntity.ok(styles);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<Page<ProfileStyleResponseDto>> getStylesByProfile(
            @PathVariable("profileId") Long profileId,
            Pageable pageable
    ) {
        Page<ProfileStyleResponseDto> styles = styleQueryService.getStylesByProfile(profileId, pageable);
        return ResponseEntity.ok(styles);
    }
}
