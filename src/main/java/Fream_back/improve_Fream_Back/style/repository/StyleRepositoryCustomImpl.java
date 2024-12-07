package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleSearchDto;
import Fream_back.improve_Fream_Back.style.entity.Style;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Fream_back.improve_Fream_Back.style.entity.QStyle.style;
import static Fream_back.improve_Fream_Back.order.entity.QOrderItem.orderItem;
import static Fream_back.improve_Fream_Back.product.entity.QProduct.product;
import static Fream_back.improve_Fream_Back.product.entity.QProductImage.productImage;

@Repository
public class StyleRepositoryCustomImpl implements StyleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public StyleRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
    @Override
    public Page<StyleResponseDto> searchStyles(StyleSearchDto searchDto, Pageable pageable) {
        List<StyleResponseDto> results = queryFactory
                .select(Projections.constructor(
                        StyleResponseDto.class,
                        style.id,
                        style.content,
                        style.rating,
                        style.imageUrl,
                        style.videoUrl,
                        style.createdDate,
                        style.user.nickname, // 유저 닉네임
                        product.id,          // 상품 ID
                        product.name,        // 상품명
                        product.brand,       // 브랜드명
                        productImage.imageUrl // 메인 썸네일 이미지 URL
                ))
                .from(style)
                .leftJoin(style.orderItem, orderItem)
                .leftJoin(orderItem.product, product)
                .leftJoin(product.userProducts).fetchJoin() // 필요한 경우 추가
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id)
                        .and(productImage.isMainThumbnail.isTrue()))
                .where(
                        userIdEq(searchDto.getUserId()),
                        productIdEq(searchDto.getProductId()),
                        keywordContains(searchDto.getKeyword())
                )
                .orderBy(style.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(style.count())
                .from(style)
                .where(
                        userIdEq(searchDto.getUserId()),
                        productIdEq(searchDto.getProductId()),
                        keywordContains(searchDto.getKeyword())
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? style.user.id.eq(userId) : null;
    }

    private BooleanExpression productIdEq(Long productId) {
        return productId != null ? orderItem.product.id.eq(productId) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        return keyword != null && !keyword.isBlank()
                ? product.name.containsIgnoreCase(keyword)
                .or(product.brand.containsIgnoreCase(keyword))
                : null;
    }

//    @Override
//    public Page<Style> searchStyles(StyleSearchDto searchDto, Pageable pageable) {
//        List<Style> results = queryFactory
//                .selectFrom(style)
//                .leftJoin(style.orderItem, orderItem).fetchJoin()
//                .leftJoin(orderItem.product, product).fetchJoin()
//                .where(
//                        userIdEq(searchDto.getUserId()),
//                        productIdEq(searchDto.getProductId()),
//                        keywordContains(searchDto.getKeyword())
//                )
//                .orderBy(style.createdDate.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long total = queryFactory
//                .select(style.count())
//                .from(style)
//                .where(
//                        userIdEq(searchDto.getUserId()),
//                        productIdEq(searchDto.getProductId()),
//                        keywordContains(searchDto.getKeyword())
//                )
//                .fetchOne();
//
//        return new PageImpl<>(results, pageable, total);
//    }
//
//    private BooleanExpression userIdEq(Long userId) {
//        return userId != null ? style.user.id.eq(userId) : null;
//    }
//
//    private BooleanExpression productIdEq(Long productId) {
//        return productId != null ? orderItem.product.id.eq(productId) : null;
//    }
//
//    private BooleanExpression keywordContains(String keyword) {
//        return keyword != null && !keyword.isBlank()
//                ? product.name.containsIgnoreCase(keyword)
//                .or(product.brand.containsIgnoreCase(keyword))
//                : null;
//    }
}