package Fream_back.improve_Fream_Back.inspection.service;

import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardCreateRequestDto;
import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardResponseDto;
import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardUpdateRequestDto;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionCategory;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandard;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandardImage;
import Fream_back.improve_Fream_Back.inspection.repository.InspectionStandardImageRepository;
import Fream_back.improve_Fream_Back.inspection.repository.InspectionStandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InspectionStandardService {

    private final InspectionStandardRepository inspectionStandardRepository;
    private final InspectionStandardImageRepository inspectionStandardImageRepository;
    private final InspectionFileStorageUtil fileStorageUtil;

    // 검수 기준 생성
    public InspectionStandardResponseDto createStandard(InspectionStandardCreateRequestDto requestDto) throws IOException {
        InspectionStandard standard = InspectionStandard.builder()
                .category(requestDto.getCategory())
                .content(requestDto.getContent())
                .build();

        InspectionStandard savedStandard = inspectionStandardRepository.save(standard);

        if (requestDto.getFiles() != null && !requestDto.getFiles().isEmpty()) {
            List<String> filePaths = saveFiles(requestDto.getFiles());
            saveStandardImages(filePaths, savedStandard);
        }

        return toResponseDto(savedStandard);
    }

    // 검수 기준 수정
    public InspectionStandardResponseDto updateStandard(Long id, InspectionStandardUpdateRequestDto requestDto) throws IOException {
        InspectionStandard standard = inspectionStandardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("검수 기준을 찾을 수 없습니다."));

        standard.update(requestDto.getCategory(), requestDto.getContent());

        List<InspectionStandardImage> existingImages = inspectionStandardImageRepository.findAllByInspectionStandardId(id);
        handleImageDeletion(existingImages, requestDto.getExistingImageUrls());

        if (requestDto.getNewFiles() != null && !requestDto.getNewFiles().isEmpty()) {
            List<String> filePaths = saveFiles(requestDto.getNewFiles());
            saveStandardImages(filePaths, standard);
        }

        return toResponseDto(standard);
    }

    // 전체 리스트 페이징 조회
    public Page<InspectionStandardResponseDto> getStandards(Pageable pageable) {
        return inspectionStandardRepository.findAllWithPaging(pageable)
                .map(this::toResponseDto);
    }

    // 단일 항목 조회
    public InspectionStandardResponseDto getStandard(Long id) {
        InspectionStandard standard = inspectionStandardRepository.findWithImagesById(id)
                .orElseThrow(() -> new IllegalArgumentException("검수 기준을 찾을 수 없습니다."));
        return toResponseDto(standard);
    }

    // 카테고리별 검수 기준 조회
    public Page<InspectionStandardResponseDto> getInspectionsByCategory(InspectionCategory category, Pageable pageable) {
        return inspectionStandardRepository.findByCategory(category, pageable)
                .map(entity -> toResponseDto(entity));
    }

    // 전체 검수 기준 조회
    public Page<InspectionStandardResponseDto> getAllInspections(Pageable pageable) {
        return inspectionStandardRepository.findAll(pageable)
                .map(entity -> toResponseDto(entity));
    }

    // 인스펙션 삭제
    public void deleteStandard(Long id) throws IOException {
        // 삭제 대상 InspectionStandard 조회
        InspectionStandard standard = inspectionStandardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("검수 기준을 찾을 수 없습니다."));

        // 관련 이미지 조회
        List<InspectionStandardImage> images = inspectionStandardImageRepository.findAllByInspectionStandardId(id);

        // 파일 삭제 및 이미지 엔티티 삭제
        for (InspectionStandardImage image : images) {
            fileStorageUtil.deleteFile(image.getImageUrl()); // 서버에서 파일 삭제
            inspectionStandardImageRepository.delete(image); // DB에서 이미지 삭제
        }

        // 인스펙션 삭제
        inspectionStandardRepository.delete(standard);
    }

    // DTO 변환
    private InspectionStandardResponseDto toResponseDto(InspectionStandard standard) {
        List<String> imageUrls = inspectionStandardImageRepository.findAllByInspectionStandardId(standard.getId())
                .stream()
                .map(InspectionStandardImage::getImageUrl)
                .collect(Collectors.toList());

        return InspectionStandardResponseDto.builder()
                .id(standard.getId())
                .category(standard.getCategory().name())
                .content(standard.getContent())
                .imageUrls(imageUrls)
                .build();
    }


    // 파일 저장
    private List<String> saveFiles(List<MultipartFile> files) throws IOException {
        return files.stream()
                .map(file -> {
                    try {
                        return fileStorageUtil.saveFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    // 이미지 삭제 처리
    private void handleImageDeletion(List<InspectionStandardImage> existingImages, List<String> existingImageUrls) throws IOException {
        for (InspectionStandardImage image : existingImages) {
            if (!existingImageUrls.contains(image.getImageUrl())) {
                fileStorageUtil.deleteFile(image.getImageUrl());
                inspectionStandardImageRepository.delete(image);
            }
        }
    }

    // 이미지 엔티티 저장
    private void saveStandardImages(List<String> filePaths, InspectionStandard standard) {
        filePaths.forEach(filePath -> {
            InspectionStandardImage image = InspectionStandardImage.builder()
                    .imageUrl(filePath)
                    .inspectionStandard(standard)
                    .build();
            inspectionStandardImageRepository.save(image);
        });
    }

    public Page<InspectionStandardResponseDto> searchStandards(String keyword, Pageable pageable) {
        return inspectionStandardRepository.searchStandards(keyword, pageable)
                .map(this::toResponseDto);
    }
}