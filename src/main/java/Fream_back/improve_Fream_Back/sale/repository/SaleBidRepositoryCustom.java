package Fream_back.improve_Fream_Back.sale.repository;

import Fream_back.improve_Fream_Back.sale.dto.SaleBidResponseDto;
import Fream_back.improve_Fream_Back.sale.dto.SaleBidStatusCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SaleBidRepositoryCustom {
    Page<SaleBidResponseDto> findSaleBidsByFilters(String email, String saleBidStatus, String saleStatus, Pageable pageable);
    SaleBidStatusCountDto countSaleBidsByStatus(String email);}
