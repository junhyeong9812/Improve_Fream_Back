package Fream_back.improve_Fream_Back.Category.repository;

import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    @Query("SELECT sc FROM SubCategory sc JOIN FETCH sc.mainCategory WHERE sc.id = :id")
    SubCategory findByIdWithMainCategory(@Param("id") Long id);

    @Query("SELECT sc FROM SubCategory sc JOIN FETCH sc.mainCategory")
    List<SubCategory> findAllWithMainCategory();
}
