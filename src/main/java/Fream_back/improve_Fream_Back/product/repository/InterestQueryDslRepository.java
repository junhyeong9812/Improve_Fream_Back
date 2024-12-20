package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InterestQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ProductSearchResponseDto> findUserInterestProducts(
            Long userId,
            SortOption sortOption, // 정렬 옵션 객체 추가
            Pageable pageable) {

        QInterest interest = QInterest.interest;
        QProduct product = QProduct.product;
        QProductColor productColor = QProductColor.productColor;
        QProductSize productSize = QProductSize.productSize;

        // QueryDSL 쿼리 생성
        JPQLQuery<Tuple> query = queryFactory.select(
                        product,
                        productColor.id,
                        productColor.thumbnailImage.imageUrl,
                        productColor.colorName,
                        productSize.purchasePrice.min(),
                        productColor.interests.size().sum().as("interestCount")
                )
                .from(interest)
                .leftJoin(interest.productColor, productColor).fetchJoin()
                .leftJoin(productColor.product, product).fetchJoin()
                .leftJoin(productColor.sizes, productSize).fetchJoin()
                .where(interest.user.id.eq(userId))
                .distinct();

        // 정렬 조건 추가
        if (sortOption != null) {
            if ("price".equalsIgnoreCase(sortOption.getField())) {
                query.orderBy("asc".equalsIgnoreCase(sortOption.getOrder())
                        ? productSize.purchasePrice.asc()
                        : productSize.purchasePrice.desc());
            } else if ("releaseDate".equalsIgnoreCase(sortOption.getField())) {
                query.orderBy("asc".equalsIgnoreCase(sortOption.getOrder())
                        ? product.releaseDate.asc()
                        : product.releaseDate.desc());
            } else if ("interestCount".equalsIgnoreCase(sortOption.getField())) {
                query.orderBy("asc".equalsIgnoreCase(sortOption.getOrder())
                        ? productColor.interests.size().sum().asc()
                        : productColor.interests.size().sum().desc());
            } else {
                query.orderBy(product.id.asc()); // 기본 정렬
            }
        } else {
            query.orderBy(product.id.asc()); // 정렬 옵션이 없을 경우 기본 정렬
        }

        // 페이징 처리
        List<Tuple> results = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 데이터 매핑
        List<ProductSearchResponseDto> content = results.stream()
                .map(tuple -> ProductSearchResponseDto.builder()
                        .id(tuple.get(product.id))
                        .name(tuple.get(product.name))
                        .englishName(tuple.get(product.englishName))
                        .releasePrice(tuple.get(product.releasePrice))
                        .thumbnailImageUrl(tuple.get(productColor.thumbnailImage.imageUrl))
                        .price(tuple.get(productSize.purchasePrice.min()))
                        .colorName(tuple.get(productColor.colorName))
                        .colorId(tuple.get(productColor.id))
                        .interestCount(tuple.get(productColor.interests.size().sum()).longValue()) // 관심 수를 Long 타입으로 변환
                        .build())
                .toList();

        // 전체 데이터 수 조회
        long total = queryFactory.select(interest.count())
                .from(interest)
                .where(interest.user.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total); // Page 객체 생성 및 반환
    }
}