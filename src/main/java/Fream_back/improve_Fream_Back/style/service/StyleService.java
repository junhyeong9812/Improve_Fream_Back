package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.repository.OrderItemRepository;
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
}
