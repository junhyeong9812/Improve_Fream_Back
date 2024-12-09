package Fream_back.improve_Fream_Back.notice.repository;

import Fream_back.improve_Fream_Back.notice.entity.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {

    // 특정 공지사항에 포함된 이미지 조회
    List<NoticeImage> findAllByNoticeId(Long noticeId);
}
