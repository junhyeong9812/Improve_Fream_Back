package Fream_back.improve_Fream_Back.inspection.repository;

import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionStandardImageRepository extends JpaRepository<InspectionStandardImage, Long> {
    List<InspectionStandardImage> findAllByInspectionStandardId(Long inspectionStandardId);
}