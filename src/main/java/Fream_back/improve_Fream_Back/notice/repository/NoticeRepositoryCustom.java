package Fream_back.improve_Fream_Back.notice.repository;

import Fream_back.improve_Fream_Back.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    Page<Notice> searchNotices(String keyword, Pageable pageable);
}
