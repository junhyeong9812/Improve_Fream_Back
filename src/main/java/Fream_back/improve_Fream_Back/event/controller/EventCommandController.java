package Fream_back.improve_Fream_Back.event.controller;

import Fream_back.improve_Fream_Back.event.dto.CreateEventRequest;
import Fream_back.improve_Fream_Back.event.dto.UpdateEventRequest;
import Fream_back.improve_Fream_Back.event.service.EventCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventCommandController {

    private final EventCommandService eventCommandService;

    /**
     * 이벤트 생성
     * @param dto : 제목, 설명, 시작/종료일시, 브랜드ID 등
     * @param thumbnailFile : 썸네일 파일
     * @param simpleImageFiles : 심플이미지 파일 목록
     */
    @PostMapping
    public ResponseEntity<Long> createEvent(
            @ModelAttribute CreateEventRequest dto,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "simpleImageFiles", required = false) List<MultipartFile> simpleImageFiles
    ) {
        Long eventId = eventCommandService.createEvent(dto, thumbnailFile, simpleImageFiles);
        return ResponseEntity.ok(eventId);
    }

    /**
     * 이벤트 수정
     * - 전체 심플이미지 "삭제 후" 새로 등록하는 방식
     */
    @PatchMapping("/{eventId}")
    public ResponseEntity<Long> updateEvent(
            @PathVariable Long eventId,
            @ModelAttribute UpdateEventRequest dto,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "simpleImageFiles", required = false) List<MultipartFile> simpleImageFiles
    ) {
        Long updatedId = eventCommandService.updateEvent(eventId, dto, thumbnailFile, simpleImageFiles);
        return ResponseEntity.ok(updatedId);
    }
    // 서버 내부에 저장된 파일을 직접 읽어서 ResponseEntity<Resource>로 응답
    @GetMapping("/{eventId}/images/{fileName}")
    public ResponseEntity<?> getEventImage(
            @PathVariable Long eventId,
            @PathVariable String fileName
    ) {
        // 디렉토리 + 파일명
        String directory = System.getProperty("user.dir") + "/event/" + eventId + "/";
        File file = new File(directory + fileName);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 파일 확장자에 따라 ContentType 설정
        // 여기서는 일단 모두 JPEG로 가정한 예시:
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.IMAGE_JPEG);

        org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(file);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
