package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StyleRepository extends JpaRepository<Style, Long>, StyleRepositoryCustom {
    // 특정 프로필 ID로 스타일 목록 조회
    List<Style> findByProfileId(Long profileId);


}
