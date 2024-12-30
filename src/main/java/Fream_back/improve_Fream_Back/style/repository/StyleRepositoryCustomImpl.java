package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.style.dto.ProfileStyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleDetailResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import Fream_back.improve_Fream_Back.style.dto.StyleFilterRequestDto;
import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.entity.QStyle;
import Fream_back.improve_Fream_Back.user.entity.QProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class StyleRepositoryCustomImpl implements StyleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<StyleResponseDto> filterStyles(StyleFilterRequestDto filterRequestDto, Pageable pageable) {
        QStyle style = QStyle.style;
        QProfile profile = QProfile.profile;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;
        QCollection collection = QCollection.collection;
        QProduct product=QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        // 필터 조건 추가
        if (filterRequestDto.getBrandName() != null) {
            builder.and(brand.name.eq(filterRequestDto.getBrandName()));
        }
        if (filterRequestDto.getCollectionName() != null) {
            builder.and(collection.name.eq(filterRequestDto.getCollectionName()));
        }
        if (filterRequestDto.getCategoryId() != null) {
            builder.and(category.id.eq(filterRequestDto.getCategoryId()));
        }
        if (filterRequestDto.getIsMainCategory() != null && filterRequestDto.getIsMainCategory()) {
            builder.and(category.parentCategory.isNull());
        }
        if (filterRequestDto.getProfileName() != null) {
            builder.and(profile.profileName.eq(filterRequestDto.getProfileName()));
        }

        // 정렬 조건
        var query = queryFactory.select(Projections.constructor(
                        StyleResponseDto.class,
                        style.id,
                        profile.profileName,
                        profile.profileImageUrl,
                        style.content,
                        style.mediaUrl,
                        style.viewCount,
                        style.likes.size() // 좋아요 수 계산
                ))
                .from(style)
                .leftJoin(style.profile, profile)
                .leftJoin(style.orderItem.productSize.productColor.product, product) // product까지만 조인
                .leftJoin(product.brand, brand) // product에서 brand 조인
                .leftJoin(product.category, category) // product에서 category 조인
                .leftJoin(product.collection, collection) // product에서 collection 조인
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬
        if ("popular".equals(filterRequestDto.getSortBy())) {
            query.orderBy(style.likes.size().desc());
        } else {
            query.orderBy(style.id.desc());
        }

        // 결과 및 페이지 계산
        List<StyleResponseDto> content = query.fetch();

        var countQuery = queryFactory.select(style.count()).from(style).where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public StyleDetailResponseDto getStyleDetail(Long styleId) {
        QStyle style = QStyle.style;
        QProfile profile = QProfile.profile;
        QProduct product = QProduct.product;
        QProductColor productColor = QProductColor.productColor;
        QProductImage productImage = QProductImage.productImage;
        QProductSize productSize = QProductSize.productSize;

        return queryFactory.select(Projections.constructor(
                        StyleDetailResponseDto.class,
                        style.id,
                        profile.profileName,
                        profile.profileImageUrl,
                        style.content,
                        style.mediaUrl,
                        style.likes.size(), // 좋아요 수
                        style.comments.size(), // 댓글 수
                        product.name,
                        product.englishName,
                        productColor.thumbnailImage.imageUrl,
                        productSize.salePrice.min() // 최저 판매가
                ))
                .from(style)
                .leftJoin(style.profile, profile)
                .leftJoin(style.orderItem.productSize.productColor.product, product)
                .leftJoin(product.colors, productColor)
                .leftJoin(productColor.thumbnailImage, productImage)
                .leftJoin(productColor.sizes, productSize)
                .where(style.id.eq(styleId))
                .fetchOne();
    }

    @Override
    public Page<ProfileStyleResponseDto> getStylesByProfile(Long profileId, Pageable pageable) {
        QStyle style = QStyle.style;
        QProfile profile = QProfile.profile;

        var query = queryFactory.select(Projections.constructor(
                        ProfileStyleResponseDto.class,
                        style.id,
                        style.mediaUrl,
                        style.likes.size() // 좋아요 수
                ))
                .from(style)
                .leftJoin(style.profile, profile)
                .where(profile.id.eq(profileId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 데이터 조회
        List<ProfileStyleResponseDto> content = query.fetch();

        // 총 카운트 계산
        var countQuery = queryFactory.select(style.count())
                .from(style)
                .leftJoin(style.profile, profile)
                .where(profile.id.eq(profileId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


}
