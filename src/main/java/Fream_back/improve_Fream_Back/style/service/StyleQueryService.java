package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.style.dto.ProfileStyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleDetailResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleFilterRequestDto;
import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.repository.StyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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



    public Page<StyleResponseDto> getFilteredStyles(StyleFilterRequestDto filterRequestDto, Pageable pageable) {
        return styleRepository.filterStyles(filterRequestDto, pageable);
    }

    public Page<ProfileStyleResponseDto> getStylesByProfile(Long profileId, Pageable pageable) {
        return styleRepository.getStylesByProfile(profileId, pageable);
    }
    // 스타일 상세 정보 조회
    public StyleDetailResponseDto getStyleDetail(Long styleId) {
        return styleRepository.getStyleDetail(styleId);
    }

}

