package Fream_back.improve_Fream_Back.Category.repository;

import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
}

