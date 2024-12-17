package Fream_back.improve_Fream_Back.inspection.service;

import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardCreateRequestDto;
import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardResponseDto;
import Fream_back.improve_Fream_Back.inspection.dto.InspectionStandardUpdateRequestDto;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandard;
import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandardImage;
import Fream_back.improve_Fream_Back.inspection.repository.InspectionStandardImageRepository;
import Fream_back.improve_Fream_Back.inspection.repository.InspectionStandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InspectionStandardCommandService {

    private final InspectionStandardRepository inspectionStandardRepository;
    private final InspectionStandardImageRepository inspectionStandardImageRepository;
    private final InspectionFileStorageUtil fileStorageUtil;

    // 검수 기준 생성
    public InspectionStandardResponseDto createStandard(InspectionStandardCreateRequestDto requestDto) throws IOException {
        // 1. InspectionStandard 엔티티 생성
        InspectionStandard standard = InspectionStandard.builder()
                .category(requestDto.getCategory())
                .content(requestDto.getContent())
                .build();

        // 2. 저장
        InspectionStandard savedStandard = inspectionStandardRepository.save(standard);

        // 3. 파일이 존재할 경우 처리
        if (fileStorageUtil.hasFiles(requestDto.getFiles())) {
            // 파일 저장
            List<String> filePaths = fileStorageUtil.saveFiles(requestDto.getFiles());

            // content 내 이미지 경로 업데이트
            String updatedContent = fileStorageUtil.updateImagePaths(requestDto.getContent(), filePaths);
            savedStandard.update(requestDto.getCategory(), updatedContent);

            // 이미지 엔티티 저장
            saveStandardImages(filePaths, savedStandard);
        }

        // 4. DTO 반환
        return toResponseDto(savedStandard);
    }

    // 검수 기준 수정
    public InspectionStandardResponseDto updateStandard(Long id, InspectionStandardUpdateRequestDto requestDto) throws IOException {
        InspectionStandard standard = inspectionStandardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("검수 기준을 찾을 수 없습니다."));

        // 기존 이미지 삭제 처리
        List<InspectionStandardImage> existingImages = inspectionStandardImageRepository.findAllByInspectionStandardId(id);
        List<String> contentImagePaths = fileStorageUtil.extractImagePaths(requestDto.getContent());

        // 삭제할 이미지 찾기 및 처리
        List<InspectionStandardImage> imagesToDelete = existingImages.stream()
                .filter(image -> !contentImagePaths.contains(image.getImageUrl()))
                .collect(Collectors.toList());
        fileStorageUtil.deleteFiles(imagesToDelete.stream().map(InspectionStandardImage::getImageUrl).toList());
        inspectionStandardImageRepository.deleteAll(imagesToDelete);

        // 새 이미지 추가 및 content 업데이트
        if (fileStorageUtil.hasFiles(requestDto.getNewFiles())) {
            List<String> newFilePaths = fileStorageUtil.saveFiles(requestDto.getNewFiles());
            String updatedContent = fileStorageUtil.updateImagePaths(requestDto.getContent(), newFilePaths);
            standard.update(requestDto.getCategory(), updatedContent);
            saveStandardImages(newFilePaths, standard);
        } else {
            standard.update(requestDto.getCategory(), requestDto.getContent());
        }

        return toResponseDto(standard);
    }

    // 검수 기준 삭제
    public void deleteStandard(Long id) throws IOException {
        InspectionStandard standard = inspectionStandardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("검수 기준을 찾을 수 없습니다."));

        List<InspectionStandardImage> images = inspectionStandardImageRepository.findAllByInspectionStandardId(id);
        fileStorageUtil.deleteFiles(images.stream().map(InspectionStandardImage::getImageUrl).toList());

        inspectionStandardImageRepository.deleteAll(images);
        inspectionStandardRepository.delete(standard);
    }

    private void saveStandardImages(List<String> filePaths, InspectionStandard standard) {
        filePaths.forEach(filePath -> {
            InspectionStandardImage image = InspectionStandardImage.builder()
                    .imageUrl(filePath)
                    .inspectionStandard(standard)
                    .build();
            inspectionStandardImageRepository.save(image);
        });
    }

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
}
