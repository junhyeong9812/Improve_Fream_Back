package Fream_back.improve_Fream_Back.faq.repository;

import Fream_back.improve_Fream_Back.faq.entity.FAQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FAQRepositoryCustom {
    Page<FAQ> searchFAQs(String keyword, Pageable pageable);
}
