package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.style.entity.MediaUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaUrlRepository extends JpaRepository<MediaUrl, Long> {
    // 특정 Style ID로 MediaUrl 목록 조회
    List<MediaUrl> findByStyleId(Long styleId);
}

