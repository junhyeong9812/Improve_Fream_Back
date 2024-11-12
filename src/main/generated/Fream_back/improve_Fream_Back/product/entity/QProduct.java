package Fream_back.improve_Fream_Back.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -748835748L;

    public static final QProduct product = new QProduct("product");

    public final Fream_back.improve_Fream_Back.base.entity.QBaseTimeEntity _super = new Fream_back.improve_Fream_Back.base.entity.QBaseTimeEntity(this);

    public final StringPath brand = createString("brand");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> initialPrice = createNumber("initialPrice", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final NumberPath<Integer> registeredCount = createNumber("registeredCount", Integer.class);

    public final StringPath sku = createString("sku");

    public final ListPath<UserProduct, QUserProduct> userProducts = this.<UserProduct, QUserProduct>createList("userProducts", UserProduct.class, QUserProduct.class, PathInits.DIRECT2);

    public QProduct(String variable) {
        super(Product.class, forVariable(variable));
    }

    public QProduct(Path<? extends Product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProduct(PathMetadata metadata) {
        super(Product.class, metadata);
    }

}

