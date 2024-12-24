package Fream_back.improve_Fream_Back.inspection.controller;

import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardCreateRequestDto;
import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardResponseDto;
import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardUpdateRequestDto;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionCategory;
import Fream_back.improve_Fream_Back.inspection.service.InspectionStandardCommandService;
import Fream_back.improve_Fream_Back.inspection.service.InspectionStandardQueryService;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/inspections")
@RequiredArgsConstructor
public class InspectionStandardController {

    private final InspectionStandardCommandService commandService;
    private final InspectionStandardQueryService queryService;
    private final UserQueryService userQueryService; // 권한 확인 서비스

    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다.");
    }
    /**
     * 검수 기준 생성 (Command)
     * @param requestDto 생성 요청 데이터
     * @return 생성된 검수 기준 DTO
     * @throws IOException 파일 처리 중 오류
     */
    @PostMapping
    public ResponseEntity<InspectionStandardResponseDto> createStandard(
            @ModelAttribute InspectionStandardCreateRequestDto requestDto) throws IOException {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        InspectionStandardResponseDto response = commandService.createStandard(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 검수 기준 수정 (Command)
     * @param id 검수 기준 ID
     * @param requestDto 수정 요청 데이터
     * @return 수정된 검수 기준 DTO
     * @throws IOException 파일 처리 중 오류
     */
    @PutMapping("/{id}")
    public ResponseEntity<InspectionStandardResponseDto> updateStandard(
            @PathVariable("id") Long id,
            @ModelAttribute InspectionStandardUpdateRequestDto requestDto) throws IOException {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        InspectionStandardResponseDto response = commandService.updateStandard(id, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 검수 기준 삭제 (Command)
     * @param id 삭제할 검수 기준 ID
     * @return 204 No Content
     * @throws IOException 파일 삭제 중 오류
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStandard(@PathVariable("id") Long id) throws IOException {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        commandService.deleteStandard(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 검수 기준 페이징 조회 (Query)
     * @param category (옵션) 카테고리 필터
     * @param pageable 페이징 정보
     * @return 필터링된 검수 기준 리스트
     */
    @GetMapping
    public ResponseEntity<Page<InspectionStandardResponseDto>> getStandards(
            @RequestParam(name = "category", required = false) InspectionCategory category,
            Pageable pageable) {
        Page<InspectionStandardResponseDto> response;

        if (category != null) {
            response = queryService.getStandardsByCategory(category, pageable);
        } else {
            response = queryService.getStandards(pageable);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 단일 검수 기준 조회 (Query)
     * @param id 조회할 검수 기준 ID
     * @return 검수 기준 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<InspectionStandardResponseDto> getStandard(@PathVariable("id") Long id) {
        InspectionStandardResponseDto response = queryService.getStandard(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 검수 기준 검색 (Query)
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색된 검수 기준 리스트
     */
//    @GetMapping("/search")
//    public ResponseEntity<Page<InspectionStandardResponseDto>> searchStandards(
//            @RequestParam String keyword,
//            Pageable pageable) {
//        Page<InspectionStandardResponseDto> response = queryService.searchStandards(keyword, pageable);
//        return ResponseEntity.ok(response);
//    }
}
