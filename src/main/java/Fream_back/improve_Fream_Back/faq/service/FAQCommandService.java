package Fream_back.improve_Fream_Back.faq.service;

import Fream_back.improve_Fream_Back.faq.dto.FAQCreateRequestDto;
import Fream_back.improve_Fream_Back.faq.dto.FAQResponseDto;
import Fream_back.improve_Fream_Back.faq.dto.FAQUpdateRequestDto;
import Fream_back.improve_Fream_Back.faq.entity.FAQ;
import Fream_back.improve_Fream_Back.faq.entity.FAQImage;
import Fream_back.improve_Fream_Back.faq.repository.FAQImageRepository;
import Fream_back.improve_Fream_Back.faq.repository.FAQRepository;
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
public class FAQCommandService {

    private final FAQRepository faqRepository;
    private final FAQImageRepository faqImageRepository;
    private final FAQFileStorageUtil fileStorageUtil;

    // FAQ 생성
    public FAQResponseDto createFAQ(FAQCreateRequestDto requestDto) throws IOException {
        FAQ faq = FAQ.builder()
                .category(requestDto.getCategory())
                .question(requestDto.getQuestion())
                .answer(requestDto.getAnswer())
                .build();

        FAQ savedFAQ = faqRepository.save(faq);

        if (fileStorageUtil.hasFiles(requestDto.getFiles())) {
            List<String> filePaths = fileStorageUtil.saveFiles(requestDto.getFiles());

            // 답변 내 <img src> 경로 수정
            String updatedAnswer = fileStorageUtil.updateImagePaths(requestDto.getAnswer(), filePaths);
            savedFAQ.update(requestDto.getCategory(), requestDto.getQuestion(), updatedAnswer);

            saveFAQImages(filePaths, savedFAQ);
        }

        return toResponseDto(savedFAQ);
    }


    // FAQ 수정
    public FAQResponseDto updateFAQ(Long id, FAQUpdateRequestDto requestDto) throws IOException {
        // 1. FAQ 조회
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));

        // 2. 기존 이미지 엔티티 조회
        List<FAQImage> existingImages = faqImageRepository.findAllByFaqId(id);

        // 3. answer에서 현재 content의 이미지 경로 추출
        List<String> contentImagePaths = fileStorageUtil.extractImagePaths(requestDto.getAnswer());

        // 4. 삭제할 이미지 구분: 기존 이미지 중 content에 존재하지 않는 이미지만 삭제
        List<FAQImage> imagesToDelete = existingImages.stream()
                .filter(image -> !contentImagePaths.contains(image.getImageUrl()))
                .collect(Collectors.toList());

        fileStorageUtil.deleteFiles(imagesToDelete.stream()
                .map(FAQImage::getImageUrl)
                .collect(Collectors.toList()));
        faqImageRepository.deleteAll(imagesToDelete);

        // 5. 새 이미지 저장 및 content 업데이트
        if (fileStorageUtil.hasFiles(requestDto.getNewFiles())) {
            List<String> newFilePaths = fileStorageUtil.saveFiles(requestDto.getNewFiles());

            // answer 내 <img src> 경로 수정 (새 이미지만 반영)
            String updatedAnswer = fileStorageUtil.updateImagePaths(requestDto.getAnswer(), newFilePaths);
            faq.update(requestDto.getCategory(), requestDto.getQuestion(), updatedAnswer);

            saveFAQImages(newFilePaths, faq);
        } else {
            // 새 이미지가 없으면 기존 content만 업데이트
            faq.update(requestDto.getCategory(), requestDto.getQuestion(), requestDto.getAnswer());
        }

        // 6. 최종 DTO 반환
        return toResponseDto(faq);
    }



    // FAQ 삭제
    public void deleteFAQ(Long id) throws IOException {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));

        List<FAQImage> images = faqImageRepository.findAllByFaqId(id);
        fileStorageUtil.deleteFiles(images.stream()
                .map(FAQImage::getImageUrl)
                .collect(Collectors.toList()));

        faqImageRepository.deleteAll(images);
        faqRepository.delete(faq);
    }

    private void saveFAQImages(List<String> filePaths, FAQ faq) {
        filePaths.forEach(filePath -> {
            FAQImage image = FAQImage.builder()
                    .imageUrl(filePath)
                    .faq(faq)
                    .build();
            faqImageRepository.save(image);
        });
    }

    private FAQResponseDto toResponseDto(FAQ faq) {
        List<String> imageUrls = faqImageRepository.findAllByFaqId(faq.getId())
                .stream()
                .map(FAQImage::getImageUrl)
                .collect(Collectors.toList());

        return FAQResponseDto.builder()
                .id(faq.getId())
                .category(faq.getCategory().name())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .imageUrls(imageUrls)
                .build();
    }
}
