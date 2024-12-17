package Fream_back.improve_Fream_Back.faq.controller;

import Fream_back.improve_Fream_Back.faq.dto.FAQCreateRequestDto;
import Fream_back.improve_Fream_Back.faq.dto.FAQResponseDto;
import Fream_back.improve_Fream_Back.faq.dto.FAQUpdateRequestDto;
import Fream_back.improve_Fream_Back.faq.entity.FAQCategory;
import Fream_back.improve_Fream_Back.faq.service.FAQCommandService;
import Fream_back.improve_Fream_Back.faq.service.FAQQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/faqs")
@RequiredArgsConstructor
public class FAQController {

    private final FAQCommandService faqCommandService; // 쓰기 작업 서비스
    private final FAQQueryService faqQueryService;     // 읽기 작업 서비스

    // FAQ 생성 (쓰기 작업)
    @PostMapping
    public ResponseEntity<FAQResponseDto> createFAQ(@ModelAttribute FAQCreateRequestDto requestDto) throws IOException {
        FAQResponseDto response = faqCommandService.createFAQ(requestDto);
        return ResponseEntity.ok(response);
    }

    // FAQ 수정 (쓰기 작업)
    @PutMapping("/{id}")
    public ResponseEntity<FAQResponseDto> updateFAQ(
            @PathVariable Long id,
            @ModelAttribute FAQUpdateRequestDto requestDto
    ) throws IOException {
        FAQResponseDto response = faqCommandService.updateFAQ(id, requestDto);
        return ResponseEntity.ok(response);
    }

    // FAQ 삭제 (쓰기 작업)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) throws IOException {
        faqCommandService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }

    // FAQ 목록 조회 (읽기 작업)
    @GetMapping
    public ResponseEntity<Page<FAQResponseDto>> getFAQs(
            @RequestParam(name = "category", required = false) FAQCategory category,
            Pageable pageable
    ) {
        Page<FAQResponseDto> response;

        if (category != null) {
            // 카테고리가 들어온 경우: 카테고리별 조회
            response = faqQueryService.getFAQsByCategory(category, pageable);
        } else {
            // 카테고리가 없는 경우: 전체 조회
            response = faqQueryService.getFAQs(pageable);
        }

        return ResponseEntity.ok(response);
    }

    // FAQ 단일 조회 (읽기 작업)
    @GetMapping("/{id}")
    public ResponseEntity<FAQResponseDto> getFAQ(@PathVariable Long id) {
        FAQResponseDto response = faqQueryService.getFAQ(id);
        return ResponseEntity.ok(response);
    }

    // FAQ 검색 (읽기 작업)
    @GetMapping("/search")
    public ResponseEntity<Page<FAQResponseDto>> searchFAQs(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Page<FAQResponseDto> results = faqQueryService.searchFAQs(keyword, pageable);
        return ResponseEntity.ok(results);
    }
}
