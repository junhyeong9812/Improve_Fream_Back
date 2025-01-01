package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.entity.StyleLike;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StyleLikeRepository extends JpaRepository<StyleLike, Long> {
    // 특정 스타일과 프로필로 좋아요 여부 확인
    Optional<StyleLike> findByStyleAndProfile(Style style, Profile profile);

    // 특정 스타일 ID와 프로필 ID로 좋아요 여부 확인
    boolean existsByStyleIdAndProfileId(Long styleId, Long profileId);

    // 특정 스타일 ID로 좋아요 목록 조회
    List<StyleLike> findByStyleId(Long styleId);
}