package Fream_back.improve_Fream_Back.product.elasticsearch.repository;

import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductColorIndexQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<ProductColor> findAllForIndexing() {
        QProductColor productColor = QProductColor.productColor;
        QProduct product = QProduct.product;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;
        QCollection collection = QCollection.collection;
        QProductSize productSize = QProductSize.productSize;
        QInterest interest = QInterest.interest;

        // productColor와 연관된 모든 정보(brand, category, collection 등) 조인
        // size들의 purchasePrice 최솟값 / 최댓값, interestCount(관심 수)도 같이 구할 수 있음
        // 아래는 예시 로직. 실제로는 groupBy, min(), max() 등 사용

        return queryFactory
                .selectFrom(productColor)
                .leftJoin(productColor.product, product).fetchJoin()
                .leftJoin(product.brand, brand).fetchJoin()
                .leftJoin(product.category, category).fetchJoin()
                .leftJoin(product.collection, collection).fetchJoin()
                .leftJoin(productColor.sizes, productSize).fetchJoin()
                .leftJoin(productColor.interests, interest).fetchJoin()
                .distinct()
                .fetch();
    }
    public ProductColor findOneForIndexing(Long colorId) {
        QProductColor productColor = QProductColor.productColor;
        QProduct product = QProduct.product;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;
        QCollection collection = QCollection.collection;
        QProductSize productSize = QProductSize.productSize;
        QInterest interest = QInterest.interest;

        return queryFactory
                .selectFrom(productColor)
                .leftJoin(productColor.product, product).fetchJoin()
                .leftJoin(product.brand, brand).fetchJoin()         // brand
                .leftJoin(product.category, category).fetchJoin()   // category
                .leftJoin(product.collection, collection).fetchJoin()  // collection
                .leftJoin(productColor.sizes, productSize).fetchJoin()
                .leftJoin(productColor.interests, interest).fetchJoin()
                .where(productColor.id.eq(colorId))
                .distinct()
                .fetchOne();
    }
}
