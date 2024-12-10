package Fream_back.improve_Fream_Back.inspection.repository;

import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InspectionStandardRepositoryCustom {
    Page<InspectionStandard> searchStandards(String keyword, Pageable pageable);
}