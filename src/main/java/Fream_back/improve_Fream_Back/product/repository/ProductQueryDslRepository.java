package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.dto.ProductDetailResponseDto;
import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    // JPAQueryFactory를 생성자로 주입받아 초기화
    public ProductQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 상품 검색 및 필터링 메서드
     *
     * @param keyword        키워드 검색 조건
     * @param categoryIds    카테고리 ID 목록
     * @param genders        성별 조건
     * @param brandIds       브랜드 ID 목록
     * @param collectionIds  컬렉션 ID 목록
     * @param colors         색상 조건
     * @param sizes          사이즈 조건
     * @param minPrice       최소 가격 조건
     * @param maxPrice       최대 가격 조건
     * @param pageable       페이징 정보
     * @return 페이징된 상품 목록
     */
    public Page<ProductSearchResponseDto> searchProducts(
            String keyword,
            List<Long> categoryIds,
            List<GenderType> genders,
            List<Long> brandIds,
            List<Long> collectionIds,
            List<String> colors,
            List<String> sizes,
            Integer minPrice,
            Integer maxPrice,
            SortOption sortOption, // 정렬 옵션 리스트
            Pageable pageable) {

        QProduct product = QProduct.product; // Product 엔티티
        QProductColor productColor = QProductColor.productColor; // ProductColor 엔티티
        QProductSize productSize = QProductSize.productSize; // ProductSize 엔티티
        QInterest interest = QInterest.interest;

        // QueryDSL 쿼리 생성
        JPQLQuery<Tuple> query = queryFactory.select(
                        product,
                        productColor.id,                         // 컬러 ID 추가
                        productColor.thumbnailImage.imageUrl,   // 썸네일
                        productColor.colorName,                 // 색상명
                        productSize.purchasePrice.min(),         // 가장 낮은 구매가
                        productColor.interests.size().sum().as("interestCount") // 관심 수 추가
                            //size().sum은 테스트 해보고 문제가 생긴다면 .count로 변경
                )
                .from(product)
                .leftJoin(product.colors, productColor).fetchJoin()
                .leftJoin(productColor.sizes, productSize).fetchJoin()
                .leftJoin(productColor.interests, interest)
                .where(
                        buildKeywordPredicate(keyword, product, productColor, productSize),
                        buildCategoryPredicate(categoryIds, product),
                        buildGenderPredicate(genders, product),
                        buildBrandPredicate(brandIds, product),
                        buildCollectionPredicate(collectionIds, product),
                        buildColorPredicate(colors, productColor),
                        buildSizePredicate(sizes, productSize),
                        buildPricePredicate(minPrice, maxPrice, productSize)
                )
                .distinct();

        // 정렬 조건 적용
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
            }
        } else {
            query.orderBy(product.id.asc()); // 기본 정렬
        }

        // 페이징 처리
        List<Tuple> results = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 데이터 매핑
        List<ProductSearchResponseDto> content = results.stream()
                .map(tuple -> {
                    Product productEntity = tuple.get(product);
                    Long colorId = tuple.get(productColor.id);                   // 컬러 ID 추가
                    String thumbnailImageUrl = tuple.get(productColor.thumbnailImage.imageUrl);
                    String colorName = tuple.get(productColor.colorName);
                    Integer lowestPrice = tuple.get(productSize.purchasePrice.min());
                    Long interestCount = tuple.get(productColor.interests.size().sum()).longValue(); // 관심 수 추가 (Integer -> Long 변환)

                    return ProductSearchResponseDto.builder()
                            .id(productEntity.getId())
                            .name(productEntity.getName())
                            .englishName(productEntity.getEnglishName())
                            .releasePrice(productEntity.getReleasePrice())
                            .thumbnailImageUrl(thumbnailImageUrl)
                            .price(lowestPrice)
                            .colorName(colorName)
                            .colorId(colorId)  // 컬러 ID 추가
                            .interestCount(interestCount) // 관심 수 추가
                            .build();
                })
                .toList();


        // 전체 데이터 수 조회
//        long total = queryFactory.select(product.count())
//                .from(product)
//                .fetchOne(); // 카운트 조회
        // 전체 데이터 수
        long total = queryFactory.select(product.id.countDistinct())
                .from(product)
                .leftJoin(product.colors, productColor)
                .where(
                        buildKeywordPredicate(keyword, product, productColor, productSize),
                        buildCategoryPredicate(categoryIds, product),
                        buildGenderPredicate(genders, product),
                        buildBrandPredicate(brandIds, product),
                        buildCollectionPredicate(collectionIds, product),
                        buildColorPredicate(colors, productColor),
                        buildSizePredicate(sizes, productSize),
                        buildPricePredicate(minPrice, maxPrice, productSize)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total); // Page 객체 생성 및 반환
    }

    // 키워드 조건 빌드
    private BooleanExpression buildKeywordPredicate(String keyword, QProduct product, QProductColor productColor, QProductSize productSize) {
        if (keyword == null || keyword.isEmpty()) {
            return null; // 키워드가 없으면 조건을 추가하지 않음
        }
        return product.name.containsIgnoreCase(keyword) // 상품명에서 키워드 검색
                .or(product.englishName.containsIgnoreCase(keyword)) // 상품 영어명에서 키워드 검색
                .or(productColor.colorName.containsIgnoreCase(keyword)) // 색상명에서 키워드 검색
                .or(productSize.size.containsIgnoreCase(keyword)); // 사이즈에서 키워드 검색
    }

    // 카테고리 조건 빌드
    private BooleanExpression buildCategoryPredicate(List<Long> categoryIds, QProduct product) {
        return categoryIds == null || categoryIds.isEmpty() ? null : product.category.id.in(categoryIds);
    }

    // 성별 조건 빌드
    private BooleanExpression buildGenderPredicate(List<GenderType> genders, QProduct product) {
        return genders == null || genders.isEmpty() ? null : product.gender.in(genders);
    }

    // 브랜드 조건 빌드
    private BooleanExpression buildBrandPredicate(List<Long> brandIds, QProduct product) {
        return brandIds == null || brandIds.isEmpty() ? null : product.brand.id.in(brandIds);
    }

    // 컬렉션 조건 빌드
    private BooleanExpression buildCollectionPredicate(List<Long> collectionIds, QProduct product) {
        return collectionIds == null || collectionIds.isEmpty() ? null : product.collection.id.in(collectionIds);
    }

    // 색상 조건 빌드
    private BooleanExpression buildColorPredicate(List<String> colors, QProductColor productColor) {
        return colors == null || colors.isEmpty() ? null : productColor.colorName.in(colors);
    }

    // 사이즈 조건 빌드
    private BooleanExpression buildSizePredicate(List<String> sizes, QProductSize productSize) {
        return sizes == null || sizes.isEmpty() ? null : productSize.size.in(sizes);
    }

    // 가격 조건 빌드
    private BooleanExpression buildPricePredicate(Integer minPrice, Integer maxPrice, QProductSize productSize) {
        BooleanExpression predicate = null;
        if (minPrice != null) {
            predicate = productSize.purchasePrice.goe(minPrice); // 최소 가격 조건
        }
        if (maxPrice != null) {
            predicate = predicate == null ? productSize.purchasePrice.loe(maxPrice) : predicate.and(productSize.purchasePrice.loe(maxPrice));
        }
        return predicate;
    }

    public ProductDetailResponseDto findProductDetail(Long productId, String colorName) {
        QProduct product = QProduct.product;
        QProductColor productColor = QProductColor.productColor;
        QProductSize productSize = QProductSize.productSize;

        // 단일 조회 쿼리
        Tuple result = queryFactory.select(
                        product,
                        productColor.id, // 컬러 ID 추가
                        productColor.thumbnailImage.imageUrl,
                        productColor.colorName,
                        productColor.content,
                        productColor.interests.size().sum().as("interestCount")
//                        productSize
                )
                .from(product)
                .leftJoin(product.colors, productColor).fetchJoin()
                .leftJoin(productColor.sizes, productSize).fetchJoin()
                .where(
                        product.id.eq(productId),
                        productColor.colorName.eq(colorName)
                )
                .fetchFirst();

        if (result == null) {
            throw new IllegalArgumentException("해당 상품 또는 색상이 존재하지 않습니다.");
        }

        // Product 정보
        Product productEntity = result.get(product);
        Long colorId = result.get(productColor.id); // 컬러 ID 가져오기
        String thumbnailImageUrl = result.get(productColor.thumbnailImage.imageUrl);
        String colorDetail = result.get(productColor.content);
        Long interestCount = result.get(productColor.interests.size().sum()).longValue();// 관심 수 가져오기


        // ProductSize 정보 리스트 생성
        List<ProductDetailResponseDto.SizeDetailDto> sizeDetails = queryFactory
                .selectFrom(productSize)
                .where(productSize.productColor.colorName.eq(colorName))
                .fetch()
                .stream()
                .map(size -> ProductDetailResponseDto.SizeDetailDto.builder()
                        .size(size.getSize())
                        .purchasePrice(size.getPurchasePrice())
                        .salePrice(size.getSalePrice())
                        .quantity(size.getQuantity())
                        .build())
                .toList();

        // DTO 생성 및 반환
        return ProductDetailResponseDto.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .englishName(productEntity.getEnglishName())
                .releasePrice(productEntity.getReleasePrice())
                .thumbnailImageUrl(thumbnailImageUrl)
                .colorId(colorId)
                .colorName(colorName)
                .content(colorDetail)
                .interestCount(interestCount)
                .sizes(sizeDetails)
                .build();
    }
}