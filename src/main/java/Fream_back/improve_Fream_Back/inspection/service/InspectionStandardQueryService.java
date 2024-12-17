package Fream_back.improve_Fream_Back.inspection.service;

import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardResponseDto;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionCategory;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandard;
import Fream_back.improve_Fream_Back.inspection.repository.InspectionStandardImageRepository;
import Fream_back.improve_Fream_Back.inspection.repository.InspectionStandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InspectionStandardQueryService {

    private final InspectionStandardRepository inspectionStandardRepository;
    private final InspectionStandardImageRepository inspectionStandardImageRepository;

    public Page<InspectionStandardResponseDto> getStandards(Pageable pageable) {
        return inspectionStandardRepository.findAll(pageable).map(this::toResponseDto);
    }

    public Page<InspectionStandardResponseDto> getStandardsByCategory(InspectionCategory category, Pageable pageable) {
        return inspectionStandardRepository.findByCategory(category, pageable)
                .map(this::toResponseDto);
    }

    public Page<InspectionStandardResponseDto> searchStandards(String keyword, Pageable pageable) {
        return inspectionStandardRepository.searchStandards(keyword, pageable).map(this::toResponseDto);
    }

    public InspectionStandardResponseDto getStandard(Long id) {
        InspectionStandard standard = inspectionStandardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("검수 기준을 찾을 수 없습니다."));
        return toResponseDto(standard);
    }

    private InspectionStandardResponseDto toResponseDto(InspectionStandard standard) {
        return InspectionStandardResponseDto.builder()
                .id(standard.getId())
                .category(standard.getCategory().name())
                .content(standard.getContent())
                .imageUrls(inspectionStandardImageRepository.findAllByInspectionStandardId(standard.getId())
                        .stream()
                        .map(image -> image.getImageUrl())
                        .toList())
                .build();
    }
}
