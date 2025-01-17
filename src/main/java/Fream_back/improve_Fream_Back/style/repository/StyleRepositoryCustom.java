package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.style.dto.ProfileStyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleDetailResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleFilterRequestDto;
import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface StyleRepositoryCustom {
    Page<StyleResponseDto> filterStyles(StyleFilterRequestDto filterRequestDto, Pageable pageable);
    StyleDetailResponseDto getStyleDetail(Long styleId);
    Page<ProfileStyleResponseDto> getStylesByProfile(Long profileId, Pageable pageable);
    Map<Long, Long> styleCountByColorIds(List<Long> colorIds);
}
