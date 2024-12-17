//package Fream_back.improve_Fream_Back.faq.service;
//
//import Fream_back.improve_Fream_Back.faq.dto.FAQCreateRequestDto;
//import Fream_back.improve_Fream_Back.faq.dto.FAQResponseDto;
//import Fream_back.improve_Fream_Back.faq.dto.FAQUpdateRequestDto;
//import Fream_back.improve_Fream_Back.faq.entity.FAQ;
//import Fream_back.improve_Fream_Back.faq.entity.FAQImage;
//import Fream_back.improve_Fream_Back.faq.repository.FAQImageRepository;
//import Fream_back.improve_Fream_Back.faq.repository.FAQRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class FAQService {
//
//    private final FAQRepository faqRepository;
//    private final FAQImageRepository faqImageRepository;
//    private final FAQFileStorageUtil fileStorageUtil;
//
//    // FAQ 생성
//    public FAQResponseDto createFAQ(FAQCreateRequestDto requestDto) throws IOException {
//        FAQ faq = FAQ.builder()
//                .category(requestDto.getCategory())
//                .question(requestDto.getQuestion())
//                .answer(requestDto.getAnswer())
//                .build();
//
//        FAQ savedFAQ = faqRepository.save(faq);
//
//        if (requestDto.getFiles() != null && !requestDto.getFiles().isEmpty()) {
//            List<String> filePaths = saveFiles(requestDto.getFiles());
//            saveFAQImages(filePaths, savedFAQ);
//        }
//
//        return toResponseDto(savedFAQ);
//    }
//
//    // FAQ 수정
//    public FAQResponseDto updateFAQ(Long id, FAQUpdateRequestDto requestDto) throws IOException {
//        FAQ faq = faqRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));
//
//        faq.update(requestDto.getCategory(), requestDto.getQuestion(), requestDto.getAnswer());
//
//        List<FAQImage> existingImages = faqImageRepository.findAllByFaqId(id);
//        handleImageDeletion(existingImages, requestDto.getExistingImageUrls());
//
//        if (requestDto.getNewFiles() != null && !requestDto.getNewFiles().isEmpty()) {
//            List<String> filePaths = saveFiles(requestDto.getNewFiles());
//            saveFAQImages(filePaths, faq);
//        }
//
//        return toResponseDto(faq);
//    }
//
//    // FAQ 삭제
//    public void deleteFAQ(Long id) throws IOException {
//        FAQ faq = faqRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));
//
//        List<FAQImage> images = faqImageRepository.findAllByFaqId(id);
//        for (FAQImage image : images) {
//            fileStorageUtil.deleteFile(image.getImageUrl());
//            faqImageRepository.delete(image);
//        }
//
//        faqRepository.delete(faq);
//    }
//
//    // FAQ 페이징 조회
//    public Page<FAQResponseDto> getFAQs(Pageable pageable) {
//        return faqRepository.findAllWithPaging(pageable)
//                .map(this::toResponseDto);
//    }
//
//    // FAQ 단일 조회
//    public FAQResponseDto getFAQ(Long id) {
//        FAQ faq = faqRepository.findWithImagesById(id)
//                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));
//        return toResponseDto(faq);
//    }
//
//    // FAQ 검색
//    public Page<FAQResponseDto> searchFAQs(String keyword, Pageable pageable) {
//        return faqRepository.searchFAQs(keyword, pageable)
//                .map(this::toResponseDto);
//    }
//
//    // DTO 변환
//    private FAQResponseDto toResponseDto(FAQ faq) {
//        List<String> imageUrls = faqImageRepository.findAllByFaqId(faq.getId())
//                .stream()
//                .map(FAQImage::getImageUrl)
//                .collect(Collectors.toList());
//
//        return FAQResponseDto.builder()
//                .id(faq.getId())
//                .category(faq.getCategory().name())
//                .question(faq.getQuestion())
//                .answer(faq.getAnswer())
//                .imageUrls(imageUrls)
//                .build();
//    }
//
//    // 파일 저장
//    private List<String> saveFiles(List<MultipartFile> files) throws IOException {
//        return files.stream()
//                .map(file -> {
//                    try {
//                        return fileStorageUtil.saveFile(file);
//                    } catch (IOException e) {
//                        throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
//                    }
//                })
//                .collect(Collectors.toList());
//    }
//
//    // 이미지 삭제 처리
//    private void handleImageDeletion(List<FAQImage> existingImages, List<String> existingImageUrls) throws IOException {
//        for (FAQImage image : existingImages) {
//            if (!existingImageUrls.contains(image.getImageUrl())) {
//                fileStorageUtil.deleteFile(image.getImageUrl());
//                faqImageRepository.delete(image);
//            }
//        }
//    }
//
//    // 이미지 엔티티 저장
//    private void saveFAQImages(List<String> filePaths, FAQ faq) {
//        filePaths.forEach(filePath -> {
//            FAQImage image = FAQImage.builder()
//                    .imageUrl(filePath)
//                    .faq(faq)
//                    .build();
//            faqImageRepository.save(image);
//        });
//    }
//}
