package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.style.dto.StyleCreateDto;
import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleSearchDto;
import Fream_back.improve_Fream_Back.style.dto.StyleUpdateDto;
import Fream_back.improve_Fream_Back.style.service.StyleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/styles")
public class StyleController {

    private final StyleService styleService;

    public StyleController(StyleService styleService) {
        this.styleService = styleService;
    }

    /**
     * [임시 파일 저장 엔드포인트]
     * - 사용자가 스타일 이미지를 임시 저장할 때 사용하는 API.
     * - 업로드된 파일을 임시 저장소에 저장하고, 저장 경로를 반환.
     * - 요청: MultipartFile 형태의 파일 (`file` 파라미터로 전달).
     * - 응답: 성공 시 파일 경로, 실패 시 오류 메시지.
     */
    @PostMapping("/upload-temp")
    public ResponseEntity<?> uploadTempFile(@RequestParam("file") MultipartFile file) {
        try {
            String tempFilePath = styleService.saveTemporaryFile(file);
            return ResponseEntity.ok(tempFilePath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * [스타일 생성 엔드포인트]
     * - 사용자가 새로운 스타일을 생성할 때 사용하는 API.
     * - 요청: StyleCreateDto에 포함된 사용자 ID, 주문 아이템 ID, 내용, 평점, 임시 파일 경로.
     * - 응답: 성공 시 생성된 스타일 ID, 실패 시 오류 메시지.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createStyle(@RequestBody StyleCreateDto createDto) {
        try {
            Long styleId = styleService.createStyle(
                    createDto.getUserId(),
                    createDto.getOrderItemId(),
                    createDto.getContent(),
                    createDto.getRating(),
                    createDto.getTempFilePath()
            );
            return ResponseEntity.ok(styleId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * [스타일 수정 엔드포인트]
     * - 특정 스타일의 내용을 수정할 때 사용하는 API.
     * - 요청: URL 경로에서 스타일 ID, RequestBody로 StyleUpdateDto 전달.
     * - 응답: 성공 시 수정된 스타일 ID, 실패 시 오류 메시지.
     */
    @PutMapping("/{styleId}/update")
    public ResponseEntity<?> updateStyle(@PathVariable Long styleId,
                                         @RequestBody StyleUpdateDto updateDto) {
        try {
            Long updatedStyleId = styleService.updateStyle(styleId, updateDto);
            return ResponseEntity.ok(updatedStyleId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * [스타일 삭제 엔드포인트]
     * - 특정 스타일을 삭제할 때 사용하는 API.
     * - 요청: URL 경로에서 스타일 ID, RequestParam으로 사용자 ID 전달.
     * - 응답: 성공 시 성공 메시지, 실패 시 오류 메시지.
     */
    @DeleteMapping("/{styleId}/delete")
    public ResponseEntity<?> deleteStyle(@PathVariable Long styleId,
                                         @RequestParam Long userId) {
        try {
            styleService.deleteStyle(styleId, userId);
            return ResponseEntity.ok("스타일이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * [스타일 검색 엔드포인트]
     * - 스타일 리스트를 조건 검색 및 페이징 처리하여 조회할 때 사용하는 API.
     * - 요청: 검색 조건 (StyleSearchDto), 페이지 번호 (page), 페이지 크기 (size).
     * - 응답: 검색된 스타일 리스트의 Page 객체 (StyleResponseDto 형태).
     */
    @PostMapping("/search")
    public Page<StyleResponseDto> searchStyles(
            @RequestBody StyleSearchDto searchDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        return styleService.getPagedStyles(searchDto, pageable);
    }

    /**
     * [스타일 상세 조회 엔드포인트]
     * - 특정 스타일의 상세 정보를 조회할 때 사용하는 API.
     * - 요청: URL 경로에서 스타일 ID 전달.
     * - 응답: 성공 시 StyleResponseDto, 실패 시 오류 메시지.
     */
    @GetMapping("/{styleId}")
    public ResponseEntity<?> getStyleById(@PathVariable Long styleId) {
        try {
            StyleResponseDto style = styleService.getStyleById(styleId);
            return ResponseEntity.ok(style);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
