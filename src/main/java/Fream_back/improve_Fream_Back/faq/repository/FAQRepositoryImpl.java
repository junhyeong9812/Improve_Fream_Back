package Fream_back.improve_Fream_Back.faq.repository;

import Fream_back.improve_Fream_Back.faq.entity.FAQ;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static Fream_back.improve_Fream_Back.faq.entity.QFAQ.fAQ;

@RequiredArgsConstructor
public class FAQRepositoryImpl implements FAQRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FAQ> searchFAQs(String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.or(fAQ.question.containsIgnoreCase(keyword))
                    .or(fAQ.answer.containsIgnoreCase(keyword));
        }

        List<FAQ> results = queryFactory.selectFrom(fAQ)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(fAQ.count())
                .from(fAQ)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
