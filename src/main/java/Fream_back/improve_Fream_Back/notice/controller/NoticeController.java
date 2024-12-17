package Fream_back.improve_Fream_Back.notice.controller;

import Fream_back.improve_Fream_Back.notice.dto.NoticeCreateRequestDto;
import Fream_back.improve_Fream_Back.notice.dto.NoticeResponseDto;
import Fream_back.improve_Fream_Back.notice.dto.NoticeUpdateRequestDto;
import Fream_back.improve_Fream_Back.notice.entity.NoticeCategory;
import Fream_back.improve_Fream_Back.notice.service.NoticeCommandService;
import Fream_back.improve_Fream_Back.notice.service.NoticeQueryService;
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

    private final NoticeCommandService noticeCommandService;
    private final NoticeQueryService noticeQueryService;

    // 공지사항 생성 (쓰기 작업)
    @PostMapping
    public ResponseEntity<NoticeResponseDto> createNotice(@ModelAttribute NoticeCreateRequestDto requestDto) throws IOException {
        NoticeResponseDto response = noticeCommandService.createNotice(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getCategory(),
                requestDto.getFiles()
        );
        return ResponseEntity.ok(response);
    }

    // 공지사항 수정 (쓰기 작업)
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> updateNotice(
            @PathVariable Long noticeId,
            @ModelAttribute NoticeUpdateRequestDto requestDto
    ) throws IOException {
        NoticeResponseDto response = noticeCommandService.updateNotice(
                noticeId,
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getCategory(),
                requestDto.getExistingImageUrls(),
                requestDto.getNewFiles()
        );
        return ResponseEntity.ok(response);
    }

    // 공지사항 삭제 (쓰기 작업)
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) throws IOException {
        noticeCommandService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }

    // 단일 공지사항 조회 (읽기 작업)
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        NoticeResponseDto response = noticeQueryService.getNotice(noticeId);
        return ResponseEntity.ok(response);
    }

    // 공지사항 검색 (읽기 작업)
    @GetMapping("/search")
    public ResponseEntity<Page<NoticeResponseDto>> searchNotices(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Page<NoticeResponseDto> results = noticeQueryService.searchNotices(keyword, pageable);
        return ResponseEntity.ok(results);
    }

    // 공지사항 카테고리별 조회 (읽기 작업)
    @GetMapping
    public ResponseEntity<Page<NoticeResponseDto>> getNoticesByCategory(
            @RequestParam(name = "category", required = false) String category,
            Pageable pageable
    ) {
        Page<NoticeResponseDto> notices;
        if (category != null) {
            NoticeCategory noticeCategory = NoticeCategory.valueOf(category); // String -> Enum 변환
            notices = noticeQueryService.getNoticesByCategory(noticeCategory, pageable);
        } else {
            notices = noticeQueryService.getNotices(pageable);
        }
        return ResponseEntity.ok(notices);
    }

    // 파일 미리보기 (읽기 작업)
    @GetMapping("/files/{fileName}")
    public ResponseEntity<byte[]> getFilePreview(@PathVariable String fileName) throws IOException {
        byte[] fileData = noticeQueryService.getFilePreview(fileName);

        String mimeType = Files.probeContentType(Paths.get("notice/" + fileName));

        return ResponseEntity.ok()
                .header("Content-Type", mimeType)
                .body(fileData);
    }
}
