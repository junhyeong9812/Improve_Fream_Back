package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.service.OrderItemQueryService;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.repository.StyleRepository;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileQueryService;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class StyleCommandService {

    private final StyleRepository styleRepository;
    private final ProfileQueryService profileQueryService;
    private final OrderItemQueryService orderItemQueryService;
    private final FileUtils fileUtils;

    private static final String STYLE_MEDIA_DIRECTORY = System.getProperty("user.dir") +  "/style/";

    // 스타일 생성
    public Style createStyle(String email, Long orderItemId, String content, MultipartFile mediaFile) {
        // 1. 프로필 조회
        Profile profile = profileQueryService.getProfileByEmail(email);

        // 2. 구매한 상품(OrderItem) 조회
        OrderItem orderItem = orderItemQueryService.findById(orderItemId);

        // 3. Style 엔티티 생성
        Style style = Style.builder()
                .profile(profile)
                .orderItem(orderItem)
                .content(content)
                .viewCount(0L) // 초기 뷰 카운트 설정
                .build();

        // 4. Profile과 Style 관계 설정
        profile.addStyle(style); // 연관관계 메서드 호출 (Profile -> Style)

        // 5. Style 저장
        Style savedStyle = styleRepository.save(style);

        // 6. 미디어 파일 저장 및 URL 설정
        if (mediaFile != null) {
            String mediaDirectory = STYLE_MEDIA_DIRECTORY + savedStyle.getId() + "/";
            String mediaUrl = fileUtils.saveFile(mediaDirectory, "media_", mediaFile);
            savedStyle.updateMediaUrl(mediaUrl);
        }

        return savedStyle;
    }

    // 뷰 카운트 증가
    public void incrementViewCount(Long styleId) {
        Style style = styleRepository.findById(styleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Style을 찾을 수 없습니다: " + styleId));
        style.incrementViewCount();
    }

    // 스타일 업데이트
    public void updateStyle(Long styleId, String content, MultipartFile mediaFile) {
        Style style = styleRepository.findById(styleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Style을 찾을 수 없습니다: " + styleId));

        // 텍스트 콘텐츠 업데이트
        if (content != null) {
            style.updateContent(content);
        }

        // 미디어 파일 업데이트
        if (mediaFile != null) {
            String mediaDirectory = STYLE_MEDIA_DIRECTORY + style.getId() + "/";
            String mediaUrl = fileUtils.saveFile(mediaDirectory, "media_", mediaFile);
            style.updateMediaUrl(mediaUrl);
        }
    }
    // 스타일 삭제
    public void deleteStyle(Long styleId) {
        Style style = styleRepository.findById(styleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Style을 찾을 수 없습니다: " + styleId));
        styleRepository.delete(style);
    }

}

