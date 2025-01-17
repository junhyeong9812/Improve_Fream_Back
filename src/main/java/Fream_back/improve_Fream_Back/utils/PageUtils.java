package Fream_back.improve_Fream_Back.utils;

import Fream_back.improve_Fream_Back.common.commonDto;
import org.springframework.data.domain.Page;

public class PageUtils {

    // Spring Data의 Page<T> → custom PageDto<T> 변환
    public static <T> commonDto.PageDto<T> toPageDto(Page<T> page) {
        commonDto.PageDto<T> pageDto = new commonDto.PageDto<>();
        pageDto.setContent(page.getContent());
        pageDto.setTotalElements(page.getTotalElements());
        pageDto.setTotalPages(page.getTotalPages());
        pageDto.setPage(page.getNumber());
        pageDto.setSize(page.getSize());
        return pageDto;
    }

}
