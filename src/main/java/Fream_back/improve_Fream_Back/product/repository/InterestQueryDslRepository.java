package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
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
            SortOption sortOption,
            Pageable pageable) {

        QInterest interest = QInterest.interest;
        QInterest interest1 = new QInterest("interest1");
        QProduct product = QProduct.product;
        QProductColor productColor = QProductColor.productColor;
        QProductSize productSize = QProductSize.productSize;
        QProductImage productImage= QProductImage.productImage;

        // 데이터 조회 쿼리 (페이징 처리)
        List<Tuple> results = queryFactory.select(
                        interest.id,
                        product.id,
                        product.name,
                        product.englishName,
                        product.releasePrice, // releasePrice 추가
                        productColor.id,
                        productColor.thumbnailImage.imageUrl,
                        productColor.colorName,
                        productSize.purchasePrice.min(),
                        JPAExpressions
                                .select(interest1.count())
                                .from(interest1)
                                .where(interest1.productColor.eq(productColor))
//                        interest1.count().as("interestCount")
                )
                .from(interest)
                .leftJoin(interest.productColor, productColor) // 일반 Join
                .leftJoin(productColor.product, product) // 일반 Join
                .leftJoin(productColor.thumbnailImage,productImage ) // Thumbnail 이미지 조인 추가
                .leftJoin(productColor.sizes, productSize) // 일반 Join
                .leftJoin(productColor.interests, interest1)
                .where(interest.user.id.eq(userId))
                .groupBy(
                        product.id,
                        productColor.id,
                        productColor.thumbnailImage.imageUrl)
                .orderBy(getOrderByClause(sortOption, product, productColor, productSize)) // 정렬 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 수 조회
        long total = queryFactory.select(interest.count())
                .from(interest)
                .where(interest.user.id.eq(userId))
                .fetchOne();

        // 결과 매핑
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
                        .interestCount(tuple.get(JPAExpressions
                                .select(interest1.count())
                                .from(interest1)
                                .where(interest1.productColor.eq(productColor)))) // 관심 수 변환
                        .build())
                .toList();

        return new PageImpl<>(content, pageable, total);
    }


    // 정렬 조건 설정
    private com.querydsl.core.types.OrderSpecifier<?> getOrderByClause(
            SortOption sortOption,
            QProduct product,
            QProductColor productColor,
            QProductSize productSize) {
        String field = sortOption != null ? sortOption.getField() : null;
        String order = sortOption != null ? sortOption.getOrder() : null;

        if (field == null || order == null) {
            return product.id.asc(); // 기본 정렬
        }

        if (sortOption != null) {
            switch (sortOption.getField()) {
                case "price":
                    return "asc".equalsIgnoreCase(sortOption.getOrder())
                            ? productSize.purchasePrice.asc()
                            : productSize.purchasePrice.desc();
                case "releaseDate":
                    return "asc".equalsIgnoreCase(sortOption.getOrder())
                            ? product.releaseDate.asc()
                            : product.releaseDate.desc();
                case "interestCount":
                    return "asc".equalsIgnoreCase(sortOption.getOrder())
                            ? productColor.interests.size().sum().asc()
                            : productColor.interests.size().sum().desc();
                default:
                    break;
            }
        }
        return product.id.asc(); // 기본 정렬
    }

//    public Page<ProductSearchResponseDto> findUserInterestProducts(
//            Long userId,
//            SortOption sortOption, // 정렬 옵션 객체 추가
//            Pageable pageable) {
//
//        QInterest interest = QInterest.interest;
//        QProduct product = QProduct.product;
//        QProductColor productColor = QProductColor.productColor;
//        QProductSize productSize = QProductSize.productSize;
//
//        // QueryDSL 쿼리 생성
//        JPQLQuery<Tuple> query = queryFactory.select(
//                        interest.id,
//                        product,
//                        productColor.id,
//                        productColor.thumbnailImage.imageUrl,
//                        productColor.colorName,
//                        productSize.purchasePrice.min(),
//                        productColor.interests.size().sum().as("interestCount")
//                )
//                .from(interest)
//                .leftJoin(interest.productColor, productColor)
//                .leftJoin(productColor.product, product)
//                .leftJoin(productColor.sizes, productSize)
//                .where(interest.user.id.eq(userId))
//                .distinct();
//
//        // 정렬 조건 추가
//        if (sortOption != null) {
//            if ("price".equalsIgnoreCase(sortOption.getField())) {
//                query.orderBy("asc".equalsIgnoreCase(sortOption.getOrder())
//                        ? productSize.purchasePrice.asc()
//                        : productSize.purchasePrice.desc());
//            } else if ("releaseDate".equalsIgnoreCase(sortOption.getField())) {
//                query.orderBy("asc".equalsIgnoreCase(sortOption.getOrder())
//                        ? product.releaseDate.asc()
//                        : product.releaseDate.desc());
//            } else if ("interestCount".equalsIgnoreCase(sortOption.getField())) {
//                query.orderBy("asc".equalsIgnoreCase(sortOption.getOrder())
//                        ? productColor.interests.size().sum().asc()
//                        : productColor.interests.size().sum().desc());
//            } else {
//                query.orderBy(product.id.asc()); // 기본 정렬
//            }
//        } else {
//            query.orderBy(product.id.asc()); // 정렬 옵션이 없을 경우 기본 정렬
//        }
//
//        // 페이징 처리
//        List<Tuple> results = query.offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        // 데이터 매핑
//        List<ProductSearchResponseDto> content = results.stream()
//                .map(tuple -> ProductSearchResponseDto.builder()
//                        .id(tuple.get(product.id))
//                        .name(tuple.get(product.name))
//                        .englishName(tuple.get(product.englishName))
//                        .releasePrice(tuple.get(product.releasePrice))
//                        .thumbnailImageUrl(tuple.get(productColor.thumbnailImage.imageUrl))
//                        .price(tuple.get(productSize.purchasePrice.min()))
//                        .colorName(tuple.get(productColor.colorName))
//                        .colorId(tuple.get(productColor.id))
//                        .interestCount(tuple.get(productColor.interests.size().sum()).longValue()) // 관심 수를 Long 타입으로 변환
//                        .build())
//                .toList();
//
//        // 전체 데이터 수 조회
//        long total = queryFactory.select(interest.count())
//                .from(interest)
//                .where(interest.user.id.eq(userId))
//                .fetchOne();
//
//        return new PageImpl<>(content, pageable, total); // Page 객체 생성 및 반환
//    }
}