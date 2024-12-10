package Fream_back.improve_Fream_Back.faq.controller;

import Fream_back.improve_Fream_Back.faq.dto.FAQCreateRequestDto;
import Fream_back.improve_Fream_Back.faq.dto.FAQResponseDto;
import Fream_back.improve_Fream_Back.faq.dto.FAQUpdateRequestDto;
import Fream_back.improve_Fream_Back.faq.service.FAQService;
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

    private final FAQService faqService;

    @PostMapping
    public ResponseEntity<FAQResponseDto> createFAQ(@ModelAttribute FAQCreateRequestDto requestDto) throws IOException {
        FAQResponseDto response = faqService.createFAQ(requestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FAQResponseDto> updateFAQ(@PathVariable Long id, @ModelAttribute FAQUpdateRequestDto requestDto) throws IOException {
        FAQResponseDto response = faqService.updateFAQ(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) throws IOException {
        faqService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<FAQResponseDto>> getFAQs(Pageable pageable) {
        Page<FAQResponseDto> response = faqService.getFAQs(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FAQResponseDto> getFAQ(@PathVariable Long id) {
        FAQResponseDto response = faqService.getFAQ(id);
        return ResponseEntity.ok(response);
    }

    // FAQ 검색
    @GetMapping("/search")
    public ResponseEntity<Page<FAQResponseDto>> searchFAQs(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Page<FAQResponseDto> results = faqService.searchFAQs(keyword, pageable);
        return ResponseEntity.ok(results);
    }
}
