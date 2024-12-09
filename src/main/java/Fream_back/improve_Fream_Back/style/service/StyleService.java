package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.repository.OrderItemRepository;
import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleSearchDto;
import Fream_back.improve_Fream_Back.style.dto.StyleUpdateDto;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.repository.StyleRepository;
import Fream_back.improve_Fream_Back.style.service.styleFileUtil.StyleFileStorageUtil;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final StyleFileStorageUtil StylefileStorageUtil;

    public StyleService(StyleRepository styleRepository, UserRepository userRepository,
                        OrderItemRepository orderItemRepository, StyleFileStorageUtil StylefileStorageUtil) {
        this.styleRepository = styleRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.StylefileStorageUtil = StylefileStorageUtil;
    }
    /**
     * [파일 임시 저장 로직]
     * - 사용자가 업로드한 파일을 임시 디렉토리에 저장.
     * - 업로드된 파일의 유효성을 검증 (null 또는 비어 있는지 확인).
     * - 임시 파일 저장 후, 저장된 파일 경로를 반환.
     */
    public String saveTemporaryFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
        }

        // 파일을 임시 디렉토리에 저장
        return StylefileStorageUtil.saveTemporaryFile(file);
    }

    /**
     * [스타일 생성 로직]
     * - 사용자와 주문 상품 정보를 검증 (존재 여부 확인).
     * - 주문 상품의 결제 상태 확인 (결제 완료된 주문만 스타일 생성 가능).
     * - Style 엔티티를 생성하고 저장소에 저장.
     * - 임시 저장소에 있는 파일을 영구 저장소로 이동.
     * - 파일 유형(이미지/비디오)에 따라 URL 설정.
     * - 생성된 Style의 ID 반환.
     */
    public Long createStyle(Long userId, Long orderItemId, String content, Integer rating, String tempFilePath) throws IOException {
        // 유저와 주문 상품 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 상품입니다."));

        // 주문 상품의 주문이 결제 완료되었는지 확인
        if (!orderItem.getOrder().isPaymentCompleted()) {
            throw new IllegalStateException("결제가 완료되지 않은 주문에 대해서는 스타일을 작성할 수 없습니다.");
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
        String finalFilePath = StylefileStorageUtil.moveToPermanentStorage(tempFilePath, savedStyle.getId());

        // 이미지 또는 비디오 URL 설정
        if (finalFilePath.endsWith(".jpg") || finalFilePath.endsWith(".jpeg") || finalFilePath.endsWith(".png") || finalFilePath.endsWith(".gif")) {
            savedStyle.updateStyle(content, rating, finalFilePath, null);
        } else if (finalFilePath.endsWith(".mp4") || finalFilePath.endsWith(".avi") || finalFilePath.endsWith(".mov")) {
            savedStyle.updateStyle(content, rating, null, finalFilePath);
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }

        return savedStyle.getId();
    }


    /**
     * [스타일 수정 로직]
     * - 스타일 ID로 기존 스타일 조회.
     * - 수정 요청한 사용자가 스타일 작성자인지 확인.
     * - 기존 파일 삭제 후, 새로운 임시 파일을 영구 저장소로 이동.
     * - 파일 유형(이미지/비디오)에 따라 URL 설정.
     * - 파일 변경이 없을 경우, 내용과 평점만 업데이트.
     * - 수정된 Style의 ID 반환.
     */
    public Long updateStyle(Long styleId, StyleUpdateDto updateDto) throws IOException {
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
                StylefileStorageUtil.deleteFile(style.getImageUrl());
            }
            if (style.getVideoUrl() != null) {
                StylefileStorageUtil.deleteFile(style.getVideoUrl());
            }

            // 새 파일 이동
            newFilePath = StylefileStorageUtil.moveToPermanentStorage(updateDto.getTempFilePath(), style.getId());
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

        return style.getId();
    }

    /**
     * [스타일 삭제 로직]
     * - 스타일 ID로 기존 스타일 조회.
     * - 삭제 요청한 사용자가 스타일 작성자인지 확인.
     * - 관련된 이미지 또는 비디오 파일 삭제.
     * - 스타일 삭제.
     */
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
            StylefileStorageUtil.deleteFile(style.getImageUrl());
        }
        if (style.getVideoUrl() != null) {
            StylefileStorageUtil.deleteFile(style.getVideoUrl());
        }

        // 스타일 삭제
        styleRepository.delete(style);
    }

    /**
     * [스타일 목록 조회 로직]
     * - 검색 조건 (StyleSearchDto)과 페이징 정보를 기반으로 스타일 목록을 조회.
     * - 검색 결과를 Page<StyleResponseDto> 형태로 반환.
     */
    public Page<StyleResponseDto> getPagedStyles(StyleSearchDto searchDto, Pageable pageable) {
        return styleRepository.searchStyles(searchDto, pageable);
    }

    /**
     * [단일 스타일 조회 로직]
     * - 스타일 ID를 기반으로 단일 스타일 조회.
     * - 결과를 StyleResponseDto 형태로 반환.
     */
    public StyleResponseDto getStyleById(Long styleId) {
        return styleRepository.findStyleById(styleId);
    }
}
