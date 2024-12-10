package Fream_back.improve_Fream_Back.inspection.repository;

import Fream_back.improve_Fream_Back.inspection.entity.InspectionStandard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static Fream_back.improve_Fream_Back.inspection.entity.QInspectionStandard.inspectionStandard;

@RequiredArgsConstructor
public class InspectionStandardRepositoryImpl implements InspectionStandardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<InspectionStandard> searchStandards(String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.or(inspectionStandard.content.containsIgnoreCase(keyword))
                    .or(inspectionStandard.category.stringValue().containsIgnoreCase(keyword));
        }

        List<InspectionStandard> results = queryFactory.selectFrom(inspectionStandard)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(inspectionStandard.count())
                .from(inspectionStandard)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
