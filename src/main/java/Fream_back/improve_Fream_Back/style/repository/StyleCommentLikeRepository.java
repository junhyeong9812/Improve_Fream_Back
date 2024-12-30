package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.style.entity.StyleComment;
import Fream_back.improve_Fream_Back.style.entity.StyleCommentLike;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StyleCommentLikeRepository extends JpaRepository<StyleCommentLike, Long> {

    // 특정 댓글과 프로필로 좋아요 여부 확인
    Optional<StyleCommentLike> findByCommentAndProfile(StyleComment comment, Profile profile);

    // 특정 댓글 ID와 프로필 ID로 좋아요 여부 확인
    boolean existsByCommentIdAndProfileId(Long commentId, Long profileId);

    // 특정 댓글 ID로 좋아요 목록 조회
    List<StyleCommentLike> findByCommentId(Long commentId);
}
