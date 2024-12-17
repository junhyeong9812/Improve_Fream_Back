package Fream_back.improve_Fream_Back.notice.service;

import Fream_back.improve_Fream_Back.notice.dto.NoticeResponseDto;
import Fream_back.improve_Fream_Back.notice.entity.Notice;
import Fream_back.improve_Fream_Back.notice.entity.NoticeCategory;
import Fream_back.improve_Fream_Back.notice.entity.NoticeImage;
import Fream_back.improve_Fream_Back.notice.repository.NoticeImageRepository;
import Fream_back.improve_Fream_Back.notice.repository.NoticeRepository;
import Fream_back.improve_Fream_Back.notification.dto.NotificationRequestDTO;
import Fream_back.improve_Fream_Back.notification.entity.NotificationCategory;
import Fream_back.improve_Fream_Back.notification.entity.NotificationType;
import Fream_back.improve_Fream_Back.notification.service.NotificationCommandService;
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
public class NoticeCommandService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final NoticeFileStorageUtil fileStorageUtil;
    private final NotificationCommandService notificationCommandService;

    // 공지사항 생성
    public NoticeResponseDto createNotice(String title, String content, NoticeCategory category, List<MultipartFile> files) throws IOException {
        Notice notice = Notice.builder()
                .title(title)
                .content(content)
                .category(category)
                .build();

        noticeRepository.save(notice);

        // 공지사항 알림 생성
        sendNotificationToAllUsers(notice);

        // 파일 저장
        // 3. 파일 저장 및 content 내 이미지 경로 업데이트
        if (files != null && !files.isEmpty()) {
            List<String> filePaths = fileStorageUtil.saveFiles(files);

            // content 내부의 <img src> 경로를 저장된 파일 경로로 수정
            String updatedContent = fileStorageUtil.updateImagePaths(content, filePaths);
            notice.updateContent(updatedContent);

            // 이미지 엔티티 저장
            saveNoticeImages(filePaths, notice);
        }

        return toResponseDto(notice);
    }

    // 공지사항 수정
//    public NoticeResponseDto updateNotice(Long noticeId, String title, String content, NoticeCategory category,
//                                          List<String> existingImageUrls, List<MultipartFile> newFiles) throws IOException {
//        Notice notice = noticeRepository.findById(noticeId)
//                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
//
//        notice.update(title, content, category);
//
//        // 기존 이미지 삭제 처리
//        List<NoticeImage> existingImages = noticeImageRepository.findAllByNoticeId(noticeId);
//        fileStorageUtil.handleImageDeletion(existingImages, existingImageUrls);
//
//        // 새 이미지 추가
//        if (newFiles != null && !newFiles.isEmpty()) {
//            List<String> newFilePaths = fileStorageUtil.saveFiles(newFiles);
//            saveNoticeImages(newFilePaths, notice);
//        }
//
//        return toResponseDto(notice);
//    }
    public NoticeResponseDto updateNotice(Long noticeId, String title, String content, NoticeCategory category,
                                          List<String> existingImageUrls, List<MultipartFile> newFiles) throws IOException {
        // 1. 공지사항 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        // 2. 기존 이미지 엔티티 조회
        List<NoticeImage> existingImages = noticeImageRepository.findAllByNoticeId(noticeId);

        // 3. Content 내 <img src> 경로 추출
        List<String> currentContentImagePaths = fileStorageUtil.extractImagePaths(content);

        // 4. 삭제할 이미지 구분: 기존 이미지 중 content에 존재하지 않는 이미지만 삭제
        List<NoticeImage> imagesToDelete = existingImages.stream()
                .filter(image -> !currentContentImagePaths.contains(image.getImageUrl()))
                .collect(Collectors.toList());

        fileStorageUtil.deleteFiles(imagesToDelete.stream()
                .map(NoticeImage::getImageUrl)
                .collect(Collectors.toList()));
        noticeImageRepository.deleteAll(imagesToDelete);

        // 5. 새로운 이미지 저장 및 경로 반영
        if (newFiles != null && !newFiles.isEmpty()) {
            List<String> newFilePaths = fileStorageUtil.saveFiles(newFiles);

            // Content 내 <img src> 경로를 새 이미지 경로로 수정
            String updatedContent = fileStorageUtil.updateImagePaths(content, newFilePaths);
            notice.update(title, updatedContent, category);

            saveNoticeImages(newFilePaths, notice);
        } else {
            // 새 이미지가 없으면 기존 Content만 업데이트
            notice.update(title, content, category);
        }

        // 6. 최종 DTO 반환
        return toResponseDto(notice);
    }

    // 공지사항 삭제
    public void deleteNotice(Long noticeId) throws IOException {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        // 기존 이미지 조회 및 삭제
        List<NoticeImage> images = noticeImageRepository.findAllByNoticeId(noticeId);
        List<String> imageUrls = images.stream()
                .map(NoticeImage::getImageUrl) // NoticeImage에서 imageUrl 추출
                .collect(Collectors.toList());

        fileStorageUtil.deleteFiles(imageUrls); // 파일 삭제

        // 이미지 엔티티 삭제
        noticeImageRepository.deleteAll(images);

        // 공지사항 삭제
        noticeRepository.delete(notice);
    }

    private void saveNoticeImages(List<String> filePaths, Notice notice) {
        filePaths.forEach(filePath -> {
            NoticeImage image = NoticeImage.builder()
                    .imageUrl(filePath)
                    .isVideo(fileStorageUtil.isVideo(filePath))
                    .notice(notice)
                    .build();
            noticeImageRepository.save(image);
        });
    }

    private NoticeResponseDto toResponseDto(Notice notice) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .category(notice.getCategory().name())
                .createdDate(notice.getCreatedDate())
                .build();
    }

    // 모든 사용자에게 알림 생성
    private void sendNotificationToAllUsers(Notice notice) {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .category(NotificationCategory.SHOPPING) // 쇼핑 카테고리
                .type(NotificationType.ANNOUNCEMENT)    // 공지사항 타입
                .message("새로운 공지사항: " + notice.getTitle())
                .build();

        notificationCommandService.createNotificationForAll(requestDTO);
    }
}
