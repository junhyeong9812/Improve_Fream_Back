package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.style.entity.StyleLike;
import Fream_back.improve_Fream_Back.style.repository.StyleLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StyleLikeQueryService {

    private final StyleLikeRepository styleLikeRepository;

    // 특정 스타일 ID와 프로필 ID로 좋아요 여부 확인
    public boolean isLikedByProfile(Long styleId, Long profileId) {
        return styleLikeRepository.existsByStyleIdAndProfileId(styleId, profileId);
    }

    // 특정 스타일 ID로 좋아요 목록 조회
    public List<StyleLike> findLikesByStyleId(Long styleId) {
        return styleLikeRepository.findByStyleId(styleId);
    }
}
