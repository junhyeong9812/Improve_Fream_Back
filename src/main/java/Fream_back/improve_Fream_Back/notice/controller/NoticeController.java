package Fream_back.improve_Fream_Back.notice.controller;

import Fream_back.improve_Fream_Back.notice.dto.NoticeCreateRequestDto;
import Fream_back.improve_Fream_Back.notice.dto.NoticeResponseDto;
import Fream_back.improve_Fream_Back.notice.dto.NoticeUpdateRequestDto;
import Fream_back.improve_Fream_Back.notice.entity.NoticeCategory;
import Fream_back.improve_Fream_Back.notice.service.NoticeCommandService;
import Fream_back.improve_Fream_Back.notice.service.NoticeQueryService;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeCommandService noticeCommandService;
    private final NoticeQueryService noticeQueryService;
    private final UserQueryService userQueryService; // 권한 확인 서비스

    private static final String NOTICE_DIRECTORY = System.getProperty("user.dir") + "/NoticeFiles/";


    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다.");
    }

    // 공지사항 생성 (쓰기 작업)
    @PostMapping
    public ResponseEntity<NoticeResponseDto> createNotice(@ModelAttribute NoticeCreateRequestDto requestDto) throws IOException {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인
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
            @PathVariable("noticeId") Long noticeId,
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
    public ResponseEntity<Void> deleteNotice(@PathVariable("noticeId") Long noticeId) throws IOException {
        noticeCommandService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }

    // 단일 공지사항 조회 (읽기 작업)
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable("noticeId") Long noticeId) {
        NoticeResponseDto response = noticeQueryService.getNotice(noticeId);
        return ResponseEntity.ok(response);
    }

    // 공지사항 검색 (읽기 작업)
    @GetMapping("/search")
    public ResponseEntity<Page<NoticeResponseDto>> searchNotices(
            @RequestParam(name = "keyword",required = false) String keyword,
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
    public ResponseEntity<byte[]> getFilePreview(@PathVariable("fileName") String fileName) throws IOException {
        byte[] fileData = noticeQueryService.getFilePreview(fileName);

        String mimeType = Files.probeContentType(Paths.get("notice/" + fileName));

        return ResponseEntity.ok()
                .header("Content-Type", mimeType)
                .body(fileData);
    }

    // 공지사항 파일 반환 (읽기 작업)
    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> getNoticeFile(@PathVariable String fileName) {
        try {
            // 파일 경로 생성
            Path filePath = Paths.get(NOTICE_DIRECTORY).resolve(fileName).normalize();

            // 파일 유효성 검사
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // 파일 리소스 반환
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IOException("파일을 읽을 수 없습니다.");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
