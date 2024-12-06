package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.repository.OrderItemRepository;
import Fream_back.improve_Fream_Back.style.dto.StyleUpdateDto;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.repository.StyleRepository;
import Fream_back.improve_Fream_Back.style.service.fileUtil.StyleFileStorageUtil;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class StyleService {

    private final StyleRepository styleRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final StyleFileStorageUtil fileStorageUtil;

    public StyleService(StyleRepository styleRepository, UserRepository userRepository,
                        OrderItemRepository orderItemRepository, StyleFileStorageUtil fileStorageUtil) {
        this.styleRepository = styleRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.fileStorageUtil = fileStorageUtil;
    }
    /**
     * 파일 임시 저장 로직
     *
     * @param file 업로드된 파일
     * @return 임시 저장된 파일 경로
     * @throws IOException 파일 저장 실패 시 예외
     */
    public String saveTemporaryFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
        }

        // 파일을 임시 디렉토리에 저장
        return fileStorageUtil.saveTemporaryFile(file);
    }

    public Style createStyle(Long userId, Long orderItemId, String content, Integer rating,
                             MultipartFile file) throws IOException {
        // 유저와 주문 상품 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 상품입니다."));

        // 주문 상품의 주문이 결제 완료되었는지 확인
        if (!orderItem.getOrder().isPaymentCompleted()) {
            throw new IllegalStateException("결제가 완료되지 않은 주문에 대해서는 스타일을 작성할 수 없습니다.");
        }

        // 파일 처리
        String tempFilePath = fileStorageUtil.saveTemporaryFile(file);
        String finalFilePath;

        // 파일 확장자 확인
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        }

        // Style 엔티티 생성 및 저장
        Style style = Style.builder()
                .user(user)
                .orderItem(orderItem)
                .content(content)
                .rating(rating)
                .build();
        Style savedStyle = styleRepository.save(style);

        // 파일을 영구 저장소로 이동
        finalFilePath = fileStorageUtil.moveToPermanentStorage(tempFilePath, savedStyle.getId());

        // 이미지 또는 비디오 URL 설정
        if (extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png") || extension.equals(".gif")) {
            savedStyle.updateStyle(content, rating, finalFilePath, null);
        } else if (extension.equals(".mp4") || extension.equals(".avi") || extension.equals(".mov")) {
            savedStyle.updateStyle(content, rating, null, finalFilePath);
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }

        return savedStyle;
    }
    // 수정 로직
    public Style updateStyle(Long styleId, StyleUpdateDto updateDto) throws IOException {
        // 스타일 조회
        Style style = styleRepository.findById(styleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스타일입니다."));

        // 사용자 검증
        if (!style.getUser().getId().equals(updateDto.getUserId())) {
            throw new IllegalStateException("해당 스타일을 수정할 권한이 없습니다.");
        }

        // 기존 파일 삭제 및 새 파일 처리
        String newFilePath = null;
        if (updateDto.getTempFilePath() != null && !updateDto.getTempFilePath().isBlank()) {
            // 기존 파일 삭제
            if (style.getImageUrl() != null) {
                fileStorageUtil.deleteFile(style.getImageUrl());
            }
            if (style.getVideoUrl() != null) {
                fileStorageUtil.deleteFile(style.getVideoUrl());
            }

            // 새 파일 이동
            newFilePath = fileStorageUtil.moveToPermanentStorage(updateDto.getTempFilePath(), style.getId());
        }

        // 파일 타입에 따라 업데이트
        if (newFilePath != null) {
            if (newFilePath.endsWith(".jpg") || newFilePath.endsWith(".jpeg") ||
                    newFilePath.endsWith(".png") || newFilePath.endsWith(".gif")) {
                style.updateStyle(updateDto.getContent(), updateDto.getRating(), newFilePath, null);
            } else if (newFilePath.endsWith(".mp4") || newFilePath.endsWith(".avi") ||
                    newFilePath.endsWith(".mov")) {
                style.updateStyle(updateDto.getContent(), updateDto.getRating(), null, newFilePath);
            } else {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
            }
        } else {
            // 파일 변경 없이 내용만 수정
            style.updateStyle(updateDto.getContent(), updateDto.getRating(), style.getImageUrl(), style.getVideoUrl());
        }

        return style;
    }

    // 삭제 로직
    public void deleteStyle(Long styleId, Long userId) throws IOException {
        // 스타일 조회
        Style style = styleRepository.findById(styleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스타일입니다."));

        // 사용자 검증
        if (!style.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 스타일을 삭제할 권한이 없습니다.");
        }

        // 관련 파일 삭제
        if (style.getImageUrl() != null) {
            fileStorageUtil.deleteFile(style.getImageUrl());
        }
        if (style.getVideoUrl() != null) {
            fileStorageUtil.deleteFile(style.getVideoUrl());
        }

        // 스타일 삭제
        styleRepository.delete(style);
    }
}
