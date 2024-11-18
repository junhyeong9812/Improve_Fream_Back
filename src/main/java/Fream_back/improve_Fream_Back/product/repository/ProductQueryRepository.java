package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.dto.ProductQueryDslResponseDto;
import Fream_back.improve_Fream_Back.product.entity.QProduct;
import Fream_back.improve_Fream_Back.product.entity.size.QProductSizeAndColorQuantity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ProductQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<ProductQueryDslResponseDto> findFilteredProducts(
            Long mainCategoryId, Long subCategoryId, String color, String size, String brand, String sortBy, Pageable pageable) {
        QProduct product = QProduct.product;
        QProductSizeAndColorQuantity sizeAndColorQuantity = QProductSizeAndColorQuantity.productSizeAndColorQuantity;

        // 1단계: Product.id 목록 조회
        JPQLQuery<Long> filteredProductIds = queryFactory
                .select(product.id)
                .from(product)
                .leftJoin(product.sizeAndColorQuantities, sizeAndColorQuantity)
                .where(
                        mainCategoryId != null ? product.mainCategory.id.eq(mainCategoryId) : null,
                        subCategoryId != null ? product.subCategory.id.eq(subCategoryId) : null,
                        brand != null ? product.brand.eq(brand) : null, // 브랜드 필터 추가
                        color != null ? sizeAndColorQuantity.color.stringValue().eq(color) : null,
                        size != null ? sizeAndColorQuantity.clothingSize.stringValue().eq(size)
                                .or(sizeAndColorQuantity.shoeSize.stringValue().eq(size)) : null
                )
                .distinct();

        // 정렬 조건
        if ("priceAsc".equals(sortBy)) {
            filteredProductIds.orderBy(product.initialPrice.asc());
        } else if ("priceDesc".equals(sortBy)) {
            filteredProductIds.orderBy(product.initialPrice.desc());
        }

        // 페이징 수동 처리
        filteredProductIds.offset(pageable.getOffset());
        filteredProductIds.limit(pageable.getPageSize());

        // Product ID 목록 및 총 개수
        List<Long> productIds = filteredProductIds.fetch();
        long total = filteredProductIds.fetchCount();

        if (productIds.isEmpty()) {
            return Page.empty();
        }

        // 2단계: 필터링된 Product.id로 연관 데이터 조회 및 그룹화
        List<ProductQueryDslResponseDto> products = new ArrayList<>();

        productIds.forEach(productId -> {
            ProductQueryDslResponseDto productDto = queryFactory
                    .select(Projections.constructor(ProductQueryDslResponseDto.class,
                            product.id,
                            product.name,
                            product.brand,
                            product.mainCategory.name,
                            product.subCategory.name,
                            null, // colors는 나중에 설정
                            null, // sizes는 나중에 설정
                            sizeAndColorQuantity.quantity.sum()))
                    .from(product)
                    .leftJoin(product.mainCategory)
                    .leftJoin(product.subCategory)
                    .leftJoin(product.sizeAndColorQuantities, sizeAndColorQuantity)
                    .where(product.id.eq(productId))
                    .distinct()
                    .fetchOne();

            if (productDto != null) {
                // colors 필드 추가
                List<String> colors = queryFactory
                        .select(sizeAndColorQuantity.color.stringValue())
                        .from(sizeAndColorQuantity)
                        .where(sizeAndColorQuantity.product.id.eq(productId))
                        .distinct()
                        .fetch();
                productDto.setColors(colors);

                // sizes 필드 추가
                List<String> sizes = queryFactory
                        .select(sizeAndColorQuantity.clothingSize.stringValue()
                                .coalesce(sizeAndColorQuantity.shoeSize.stringValue()))
                        .from(sizeAndColorQuantity)
                        .where(sizeAndColorQuantity.product.id.eq(productId))
                        .distinct()
                        .fetch();
                productDto.setSizes(sizes);

                products.add(productDto);
            }
        });

        return new PageImpl<>(products, pageable, total);
    }
}
