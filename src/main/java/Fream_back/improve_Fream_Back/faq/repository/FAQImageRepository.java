package Fream_back.improve_Fream_Back.faq.repository;

import Fream_back.improve_Fream_Back.faq.entity.FAQImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FAQImageRepository extends JpaRepository<FAQImage, Long> {
    List<FAQImage> findAllByFaqId(Long faqId);
}
