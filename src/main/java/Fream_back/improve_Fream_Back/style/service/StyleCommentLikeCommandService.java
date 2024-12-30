package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.style.entity.StyleComment;
import Fream_back.improve_Fream_Back.style.entity.StyleCommentLike;
import Fream_back.improve_Fream_Back.style.repository.StyleCommentLikeRepository;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StyleCommentLikeCommandService {

    private final StyleCommentLikeRepository styleCommentLikeRepository;
    private final ProfileQueryService profileQueryService;
    private final StyleCommentQueryService styleCommentQueryService;

    // 댓글 좋아요 상태 토글
    public void toggleCommentLike(String email, Long commentId) {
        // 1. 프로필 조회
        Profile profile = profileQueryService.getProfileByEmail(email);

        // 2. 댓글 조회
        StyleComment comment = styleCommentQueryService.findById(commentId);

        // 3. 기존 좋아요 엔티티 조회
        StyleCommentLike existingLike = styleCommentLikeRepository.findByCommentAndProfile(comment, profile).orElse(null);

        if (existingLike != null) {
            // 이미 좋아요 상태인 경우 삭제
            styleCommentLikeRepository.delete(existingLike);
        } else {
            // 좋아요 추가
            StyleCommentLike commentLike = StyleCommentLike.builder()
                    .comment(comment)
                    .profile(profile)
                    .build();
            styleCommentLikeRepository.save(commentLike);
        }
    }
}
