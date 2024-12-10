package Fream_back.improve_Fream_Back.notice.repository;

import Fream_back.improve_Fream_Back.notice.entity.Notice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static Fream_back.improve_Fream_Back.notice.entity.QNotice.notice;

@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notice> searchNotices(String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.or(notice.title.containsIgnoreCase(keyword))
                    .or(notice.content.containsIgnoreCase(keyword));
        }

        List<Notice> results = queryFactory.selectFrom(notice)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(notice.count())
                .from(notice)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
