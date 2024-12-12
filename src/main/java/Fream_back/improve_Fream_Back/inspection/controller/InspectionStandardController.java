package Fream_back.improve_Fream_Back.inspection.controller;

import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardCreateRequestDto;
import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardResponseDto;
import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardUpdateRequestDto;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionCategory;
import Fream_back.improve_Fream_Back.inspection.service.InspectionStandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/inspections")
@RequiredArgsConstructor
public class InspectionStandardController {

    private final InspectionStandardService inspectionStandardService;

    /**
     * 검수 기준 생성
     * @param requestDto 요청 데이터 (category, content, files)
     * @return 생성된 검수 기준의 응답 DTO
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    @PostMapping
    public ResponseEntity<InspectionStandardResponseDto> createStandard(
            @ModelAttribute InspectionStandardCreateRequestDto requestDto) throws IOException {
        InspectionStandardResponseDto response = inspectionStandardService.createStandard(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 검수 기준 수정
     * @param id 수정할 검수 기준 ID
     * @param requestDto 요청 데이터 (category, content, 기존 이미지, 새 파일)
     * @return 수정된 검수 기준의 응답 DTO
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    @PutMapping("/{id}")
    public ResponseEntity<InspectionStandardResponseDto> updateStandard(
            @PathVariable Long id,
            @ModelAttribute InspectionStandardUpdateRequestDto requestDto) throws IOException {
        InspectionStandardResponseDto response = inspectionStandardService.updateStandard(id, requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 검수 기준 삭제
     * @param id 삭제할 검수 기준 ID
     * @return 성공 시 204 No Content 반환
     * @throws IOException 파일 삭제 중 오류 발생 시
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStandard(@PathVariable Long id) throws IOException {
        inspectionStandardService.deleteStandard(id); // deleteStandard 메서드 구현 필요
        return ResponseEntity.noContent().build();
    }

    /**
     * 검수 기준 페이징 조회
     * @param pageable 페이징 정보
     * @return 페이징 처리된 검수 기준 리스트
     */
    @GetMapping
    public ResponseEntity<Page<InspectionStandardResponseDto>> getInspections(
            @RequestParam(name = "category", required = false) String category,
            Pageable pageable
    ) {
        Page<InspectionStandardResponseDto> standards;

        if (category != null) {
            // 카테고리 String -> Enum 변환
            InspectionCategory inspectionCategory = InspectionCategory.valueOf(category);
            standards = inspectionStandardService.getInspectionsByCategory(inspectionCategory, pageable);
        } else {
            // 카테고리 필터 없이 전체 조회
            standards = inspectionStandardService.getAllInspections(pageable);
        }

        return ResponseEntity.ok(standards);
    }

    /**
     * 단일 검수 기준 조회
     * @param id 조회할 검수 기준 ID
     * @return 검수 기준 응답 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<InspectionStandardResponseDto> getStandard(@PathVariable Long id) {
        InspectionStandardResponseDto response = inspectionStandardService.getStandard(id); // getStandard 메서드 구현 필요
        return ResponseEntity.ok(response);
    }

    /**
     * 검수 기준 검색
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색된 검수 기준 리스트
     */
    @GetMapping("/search")
    public ResponseEntity<Page<InspectionStandardResponseDto>> searchStandards(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<InspectionStandardResponseDto> results = inspectionStandardService.searchStandards(keyword, pageable);
        return ResponseEntity.ok(results);
    }
}
