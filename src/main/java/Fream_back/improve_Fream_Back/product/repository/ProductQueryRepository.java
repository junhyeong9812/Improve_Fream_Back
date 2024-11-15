package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.dto.ProductQueryDslResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.QProduct;
import Fream_back.improve_Fream_Back.product.entity.size.QProductSizeAndColorQuantity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ProductQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<ProductQueryDslResponseDto> findProductsByFilter(Long mainCategoryId, Long subCategoryId, String color, String size) {
        QProduct product = QProduct.product;
        QProductSizeAndColorQuantity sizeAndColorQuantity = QProductSizeAndColorQuantity.productSizeAndColorQuantity;

        return queryFactory.select(Projections.constructor(ProductQueryDslResponseDto.class,
                        product.id,
                        product.name,
                        product.brand,
                        product.mainCategory.name,
                        product.subCategory.name,
                        sizeAndColorQuantity.color.stringValue(),
                        sizeAndColorQuantity.clothingSize.stringValue().coalesce(sizeAndColorQuantity.shoeSize.stringValue()),
                        sizeAndColorQuantity.quantity))
                .from(product)
                .leftJoin(product.mainCategory)
                .leftJoin(product.subCategory)
                .leftJoin(product.sizeAndColorQuantities, sizeAndColorQuantity)
                .where(
                        mainCategoryId != null ? product.mainCategory.id.eq(mainCategoryId) : null,
                        subCategoryId != null ? product.subCategory.id.eq(subCategoryId) : null,
                        color != null ? sizeAndColorQuantity.color.stringValue().eq(color) : null,
                        size != null ? sizeAndColorQuantity.clothingSize.stringValue().eq(size)
                                .or(sizeAndColorQuantity.shoeSize.stringValue().eq(size)) : null
                )
                .distinct()
                .fetch();
    }
}