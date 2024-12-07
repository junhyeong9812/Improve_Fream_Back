package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.entity.Style;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long>, StyleRepositoryCustom {

    // 특정 사용자와 연관된 스타일 목록 조회
    @Query("SELECT s FROM Style s WHERE s.user.id = :userId")
    List<Style> findByUserId(@Param("userId") Long userId);

    // 특정 주문 상품에 대한 스타일 조회
    @Query("SELECT s FROM Style s WHERE s.orderItem.id = :orderItemId")
    List<Style> findByOrderItemId(@Param("orderItemId") Long orderItemId);

    // 특정 유저와 특정 상품에 대한 스타일 조회
    @Query("SELECT s FROM Style s WHERE s.user.id = :userId AND s.orderItem.id = :orderItemId")
    List<Style> findByUserAndOrderItem(@Param("userId") Long userId, @Param("orderItemId") Long orderItemId);

    //스타일 상세 정보 조회
    @Query("""
        SELECT new Fream_back.improve_Fream_Back.style.dto.StyleResponseDto(
            s.id, s.content, s.rating, s.imageUrl, s.videoUrl, s.createdDate,
            u.nickname, 
            p.id, p.name, p.brand, pi.imageUrl
        )
        FROM Style s
        LEFT JOIN s.user u
        LEFT JOIN s.orderItem oi
        LEFT JOIN oi.product p
        LEFT JOIN p.userProducts up
        LEFT JOIN ProductImage pi ON pi.product.id = p.id AND pi.isMainThumbnail = true
        WHERE s.id = :styleId
    """)
    StyleResponseDto findStyleById(@Param("styleId") Long styleId);

}
