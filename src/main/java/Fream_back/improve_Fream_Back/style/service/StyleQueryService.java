package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.repository.StyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StyleQueryService {

    private final StyleRepository styleRepository;

    // 스타일 ID로 조회
    public Style findStyleById(Long styleId) {
        return styleRepository.findById(styleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스타일을 찾을 수 없습니다: " + styleId));
    }

    // 특정 프로필 ID로 스타일 목록 조회
    public List<Style> findStylesByProfileId(Long profileId) {
        return styleRepository.findByProfileId(profileId);
    }

    // 특정 OrderItem ID로 스타일 목록 조회
    public List<Style> findStylesByOrderItemId(Long orderItemId) {
        return styleRepository.findByOrderItemId(orderItemId);
    }
}

