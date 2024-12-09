package Fream_back.improve_Fream_Back.notice.repository;

import Fream_back.improve_Fream_Back.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 단일 조회: Notice와 NoticeImage를 함께 조회
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.images WHERE n.id = :id")
    Notice findByIdWithImages(@Param("id") Long id);

    // 페이징 처리된 Notice 목록 조회
    @Query("SELECT n FROM Notice n")
    Page<Notice> findAllWithPaging(Pageable pageable);
}