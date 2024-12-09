package Fream_back.improve_Fream_Back.inspection.repository;

import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InspectionStandardRepository extends JpaRepository<InspectionStandard, Long> {
    // 단일 조회 시 이미지까지 조인하여 가져오기
    @Query("SELECT is FROM InspectionStandard is " +
            "LEFT JOIN FETCH is.images " +
            "WHERE is.id = :id")
    Optional<InspectionStandard> findWithImagesById(@Param("id") Long id);

    // 페이징 목록 조회
    @Query("SELECT is FROM InspectionStandard is")
    Page<InspectionStandard> findAllWithPaging(Pageable pageable);
}
