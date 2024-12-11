package Fream_back.improve_Fream_Back.notice.controller;

import Fream_back.improve_Fream_Back.notice.dto.NoticeCreateRequestDto;
import Fream_back.improve_Fream_Back.notice.dto.NoticeResponseDto;
import Fream_back.improve_Fream_Back.notice.dto.NoticeUpdateRequestDto;
import Fream_back.improve_Fream_Back.notice.entity.Notice;
import Fream_back.improve_Fream_Back.notice.entity.NoticeCategory;
import Fream_back.improve_Fream_Back.notice.repository.NoticeRepository;
import Fream_back.improve_Fream_Back.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 생성
    @PostMapping
    public ResponseEntity<NoticeResponseDto> createNotice(@ModelAttribute NoticeCreateRequestDto requestDto) throws IOException {
        NoticeResponseDto response = noticeService.createNotice(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getCategory(),
                requestDto.getFiles()
        );
        return ResponseEntity.ok(response);
    }

    // 공지사항 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> updateNotice(
            @PathVariable Long noticeId,
            @ModelAttribute NoticeUpdateRequestDto requestDto
    ) throws IOException {
        NoticeResponseDto response = noticeService.updateNotice(
                noticeId,
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getCategory(),
                requestDto.getExistingImageUrls(),
                requestDto.getNewFiles()
        );
        return ResponseEntity.ok(response);
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) throws IOException {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }

    // 단일 공지사항 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        NoticeResponseDto response = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(response);
    }

    // 공지사항 검색
    @GetMapping("/search")
    public ResponseEntity<Page<NoticeResponseDto>> searchNotices(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Page<NoticeResponseDto> results = noticeService.searchNotices(keyword, pageable);
        return ResponseEntity.ok(results);
    }

    // 파일 미리보기
    @GetMapping("/files/{fileName}")
    public ResponseEntity<byte[]> getFilePreview(@PathVariable String fileName) throws IOException {
        byte[] fileData = noticeService.getFilePreview(fileName);

        String mimeType = Files.probeContentType(Paths.get("notice/" + fileName));

        return ResponseEntity.ok()
                .header("Content-Type", mimeType)
                .body(fileData);
    }
    @GetMapping
    public ResponseEntity<Page<NoticeResponseDto>> getNoticesByCategory(
            @RequestParam(name = "category",required = false) String category,
            Pageable pageable
    ) {
        Page<NoticeResponseDto> notices;
        if (category != null) {
            NoticeCategory noticeCategory = NoticeCategory.valueOf(category); // String -> Enum 변환
            notices = noticeService.getNoticesByCategory(noticeCategory, pageable);
        } else {
            notices = noticeService.getNotices(pageable);
        }
        return ResponseEntity.ok(notices);
    }


}