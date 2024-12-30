package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.entity.StyleComment;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StyleCommentRepository extends JpaRepository<StyleComment, Long> {
    // 특정 스타일 ID로 연결된 댓글 목록 조회
    List<StyleComment> findByStyleId(Long styleId);

    // 특정 프로필 ID로 작성된 댓글 목록 조회
    List<StyleComment> findByProfileId(Long profileId);
}
