package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.order.entity.QOrderItem;
import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.style.dto.ProfileStyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleDetailResponseDto;
import Fream_back.improve_Fream_Back.style.entity.QMediaUrl;
import Fream_back.improve_Fream_Back.style.entity.QStyleOrderItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
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
        QStyleOrderItem styleOrderItem = QStyleOrderItem.styleOrderItem;
        QOrderItem orderItem = QOrderItem.orderItem;
        QMediaUrl mediaUrl = QMediaUrl.mediaUrl;

        BooleanBuilder builder = new BooleanBuilder();

        // 필터 조건 추가
        if (filterRequestDto.getBrandName() != null) {
            builder.and(orderItem.productSize.productColor.product.brand.name.eq(filterRequestDto.getBrandName()));
        }
        if (filterRequestDto.getCollectionName() != null) {
            builder.and(orderItem.productSize.productColor.product.collection.name.eq(filterRequestDto.getCollectionName()));
        }
        if (filterRequestDto.getCategoryId() != null) {
            builder.and(orderItem.productSize.productColor.product.category.id.eq(filterRequestDto.getCategoryId()));
        }
        if (filterRequestDto.getProfileName() != null) {
            builder.and(profile.profileName.eq(filterRequestDto.getProfileName()));
        }

        // 쿼리 생성
        var query = queryFactory.select(Projections.constructor(
                        StyleResponseDto.class,
                        style.id,
                        profile.profileName,
                        profile.profileImageUrl,
                        style.content,
                        // 가장 먼저 저장된 MediaUrl
                        JPAExpressions.select(mediaUrl.url)
                                .from(mediaUrl)
                                .where(mediaUrl.style.eq(style))
                                .orderBy(mediaUrl.id.asc())
                                .limit(1),
                        style.viewCount,
                        style.likes.size() // 좋아요 수 계산
                ))
                .from(style)
                .leftJoin(style.profile, profile)
                .leftJoin(style.styleOrderItems, styleOrderItem)
                .leftJoin(styleOrderItem.orderItem, orderItem)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬
        if ("popular".equals(filterRequestDto.getSortBy())) {
            query.orderBy(style.likes.size().desc());
        } else {
            query.orderBy(style.id.desc());
        }

        // 데이터 조회
        List<StyleResponseDto> content = query.fetch();

        // 총 카운트 계산
        var countQuery = queryFactory.select(style.count())
                .from(style)
                .leftJoin(style.styleOrderItems, styleOrderItem)
                .leftJoin(styleOrderItem.orderItem, orderItem)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    @Override
    public StyleDetailResponseDto getStyleDetail(Long styleId) {
        QStyle style = QStyle.style;
        QProfile profile = QProfile.profile;
        QMediaUrl mediaUrl = QMediaUrl.mediaUrl;
        QStyleOrderItem styleOrderItem = QStyleOrderItem.styleOrderItem;
        QOrderItem orderItem = QOrderItem.orderItem;
        QProduct product = QProduct.product;
        QProductColor productColor = QProductColor.productColor;
        QProductImage productImage = QProductImage.productImage;
        QProductSize productSize = QProductSize.productSize;

        // 1. 미디어 URL 목록 조회
        List<String> mediaUrls = queryFactory
                .select(mediaUrl.url)
                .from(mediaUrl)
                .where(mediaUrl.style.id.eq(styleId))
                .orderBy(mediaUrl.id.asc())
                .fetch();

        // 2. ProductInfoDto 목록 조회
        List<StyleDetailResponseDto.ProductInfoDto> productInfos = queryFactory
                .select(Projections.constructor(
                        StyleDetailResponseDto.ProductInfoDto.class,
                        product.name,
                        product.englishName,
                        productColor.thumbnailImage.imageUrl,
                        productSize.salePrice.min() // 최저 판매가
                ))
                .from(styleOrderItem)
                .leftJoin(styleOrderItem.orderItem, orderItem)
                .leftJoin(orderItem.productSize.productColor.product, product)
                .leftJoin(product.colors, productColor)
                .leftJoin(productColor.thumbnailImage, productImage)
                .leftJoin(productColor.sizes, productSize)
                .where(styleOrderItem.style.id.eq(styleId))
                .fetch();

        // 3. 스타일 정보 조회
        StyleDetailResponseDto styleDetail = queryFactory
                .select(Projections.constructor(
                        StyleDetailResponseDto.class,
                        style.id,
                        profile.profileName,
                        profile.profileImageUrl,
                        style.content,
                        null, // 미디어 URL은 나중에 설정
                        style.likes.size(), // 좋아요 수
                        style.comments.size(), // 댓글 수
                        null // 상품 정보는 나중에 설정
                ))
                .from(style)
                .leftJoin(style.profile, profile)
                .where(style.id.eq(styleId))
                .fetchOne();

        // 4. 미디어 URL과 상품 정보를 DTO에 주입
        if (styleDetail != null) {
            styleDetail.setMediaUrls(mediaUrls);
            styleDetail.setProductInfos(productInfos);
        }

        return styleDetail;
    }


    @Override
    public Page<ProfileStyleResponseDto> getStylesByProfile(Long profileId, Pageable pageable) {
        QStyle style = QStyle.style;
        QProfile profile = QProfile.profile;
        QMediaUrl mediaUrl = QMediaUrl.mediaUrl;

        // 첫 번째 미디어 URL 조회
        var query = queryFactory.select(Projections.constructor(
                        ProfileStyleResponseDto.class,
                        style.id,
                        JPAExpressions.select(mediaUrl.url)
                                .from(mediaUrl)
                                .where(mediaUrl.style.id.eq(style.id))
                                .orderBy(mediaUrl.id.asc()) // 첫 번째 URL 가져오기
                                .limit(1),
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
