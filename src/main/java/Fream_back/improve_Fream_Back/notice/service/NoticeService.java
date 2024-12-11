package Fream_back.improve_Fream_Back.notice.service;

import Fream_back.improve_Fream_Back.notice.dto.NoticeResponseDto;
import Fream_back.improve_Fream_Back.notice.entity.Notice;
import Fream_back.improve_Fream_Back.notice.entity.NoticeCategory;
import Fream_back.improve_Fream_Back.notice.entity.NoticeImage;
import Fream_back.improve_Fream_Back.notice.repository.NoticeImageRepository;
import Fream_back.improve_Fream_Back.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final NoticeFileStorageUtil fileStorageUtil;

    // 공지사항 생성
    public NoticeResponseDto createNotice(String title, String content, NoticeCategory category, List<MultipartFile> files) throws IOException {
        // 공지사항 엔티티 생성
        Notice notice = Notice.builder()
                .title(title)
                .content(content)
                .category(category)
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        // 파일 저장 및 이미지 URL 변경
        if (files != null && !files.isEmpty()) {
            List<String> filePaths = saveFiles(files);

            // content 내부의 <img src=""> 경로 수정
            String updatedContent = updateContentImagePaths(content, filePaths);
            savedNotice.updateContent(updatedContent);

            // 이미지 엔티티 저장
            saveNoticeImages(filePaths, savedNotice);
        }

        return toResponseDto(savedNotice);
    }

    // 공지사항 수정
    public NoticeResponseDto updateNotice(Long noticeId, String title, String content, NoticeCategory category,
                               List<String> existingImageUrls, List<MultipartFile> newFiles) throws IOException {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        // 공지사항 업데이트
        notice.update(title, content, category);

        // 기존 이미지 처리
        List<NoticeImage> existingImages = noticeImageRepository.findAllByNoticeId(noticeId);
        handleImageDeletion(existingImages, existingImageUrls);

        // 새로운 이미지 저장
        if (newFiles != null && !newFiles.isEmpty()) {
            List<String> newFilePaths = saveFiles(newFiles);

            // content 내부 <img src=""> 경로 수정
            String updatedContent = updateContentImagePaths(content, newFilePaths);
            notice.updateContent(updatedContent);

            // 새로운 이미지 엔티티 저장
            saveNoticeImages(newFilePaths, notice);
        }

        return toResponseDto(notice);
    }

    // 공지사항 삭제
    public void deleteNotice(Long noticeId) throws IOException {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        // 관련 이미지 삭제
        List<NoticeImage> images = noticeImageRepository.findAllByNoticeId(noticeId);
        for (NoticeImage image : images) {
            fileStorageUtil.deleteFile(image.getImageUrl());
            noticeImageRepository.delete(image);
        }

        noticeRepository.delete(notice);
    }

    // 공지사항 목록 조회 (페이징)
    public Page<NoticeResponseDto> getNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    // 단일 공지사항 조회
    public NoticeResponseDto getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        return toResponseDto(notice);
    }

    // Notice -> NoticeResponseDto 변환
    private NoticeResponseDto toResponseDto(Notice notice) {
        List<String> imageUrls = noticeImageRepository.findAllByNoticeId(notice.getId()).stream()
                .map(NoticeImage::getImageUrl)
                .collect(Collectors.toList());

        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .category(notice.getCategory().name())
                .createdDate(notice.getCreatedDate())
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

    // 이미지 경로를 content에 반영
    private String updateContentImagePaths(String content, List<String> filePaths) {
        String regex = "<img\\s+[^>]*src=\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        StringBuffer updatedContent = new StringBuffer();
        while (matcher.find()) {
            String originalSrc = matcher.group(1); // 기존 src 속성 값
            String newSrc = filePaths.isEmpty() ? originalSrc : filePaths.remove(0);
            matcher.appendReplacement(updatedContent, matcher.group(0).replace(originalSrc, newSrc));
        }
        matcher.appendTail(updatedContent);

        return updatedContent.toString();
    }

    // 이미지 엔티티 저장
    private void saveNoticeImages(List<String> filePaths, Notice notice) {
        filePaths.forEach(filePath -> {
            NoticeImage image = NoticeImage.builder()
                    .imageUrl(filePath)
                    .isVideo(isVideo(filePath))
                    .notice(notice)
                    .build();
            noticeImageRepository.save(image);
        });
    }

    // 기존 이미지 삭제 처리
    private void handleImageDeletion(List<NoticeImage> existingImages, List<String> existingImageUrls) throws IOException {
        for (NoticeImage image : existingImages) {
            if (!existingImageUrls.contains(image.getImageUrl())) {
                fileStorageUtil.deleteFile(image.getImageUrl());
                noticeImageRepository.delete(image);
            }
        }
    }

    // 비디오 판별
    private boolean isVideo(String filePath) {
        String lowerCasePath = filePath.toLowerCase();
        return lowerCasePath.endsWith(".mp4") || lowerCasePath.endsWith(".avi") || lowerCasePath.endsWith(".mov");
    }

    //검색 쿼리
    public Page<NoticeResponseDto> searchNotices(String keyword, Pageable pageable) {
        return noticeRepository.searchNotices(keyword, pageable)
                .map(this::toResponseDto);
    }

    //파일 응답
    public byte[] getFilePreview(String fileName) throws IOException {
        Path filePath = fileStorageUtil.getFilePath(fileName);

        if (!Files.exists(filePath)) {
            throw new NoSuchFileException("파일을 찾을 수 없습니다: " + fileName);
        }

        return Files.readAllBytes(filePath);
    }

    //카테고리 필터링
    public Page<NoticeResponseDto> getNoticesByCategory(NoticeCategory category, Pageable pageable) {
        return noticeRepository.findByCategory(category, pageable)
                .map(notice -> NoticeResponseDto.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .content(notice.getContent())
                        .category(notice.getCategory().toString()) // Enum -> String 변환
                        .createdDate(notice.getCreatedDate())
                        .updatedDate(notice.getModifiedDate())
                        .imageUrls(getImageUrls(notice.getId())) // 이미지 URL 리스트 추가
                        .build());
    }

    // 이미지 URL 리스트를 반환하는 메서드 추가
    private List<String> getImageUrls(Long noticeId) {
        return noticeImageRepository.findAllByNoticeId(noticeId).stream()
                .map(NoticeImage::getImageUrl)
                .toList(); // NoticeImage에서 imageUrl만 추출
    }

}