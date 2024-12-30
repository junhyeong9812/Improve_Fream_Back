package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.entity.StyleLike;
import Fream_back.improve_Fream_Back.style.repository.StyleLikeRepository;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StyleLikeCommandService {

    private final StyleLikeRepository styleLikeRepository;
    private final ProfileQueryService profileQueryService;
    private final StyleQueryService styleQueryService;

    // 좋아요 추가 또는 취소
    public void toggleLike(String email, Long styleId) {
        Profile profile = profileQueryService.getProfileByEmail(email);
        Style style = styleQueryService.findStyleById(styleId);

        // 기존 좋아요 조회
        StyleLike existingLike = styleLikeRepository.findByStyleAndProfile(style, profile).orElse(null);

        if (existingLike != null) {
            styleLikeRepository.delete(existingLike);
            style.removeLike(existingLike); // Style -> Like 관계 제거
        } else {
            // 새로운 좋아요 추가
            StyleLike styleLike = StyleLike.builder()
                    .style(style)
                    .profile(profile)
                    .build();

            style.addLike(styleLike); // Style -> Like 관계 설정
            styleLikeRepository.save(styleLike);
        }
    }
}

