package Fream_back.improve_Fream_Back.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserProduct is a Querydsl query type for UserProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserProduct extends EntityPathBase<UserProduct> {

    private static final long serialVersionUID = -1906831471L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserProduct userProduct = new QUserProduct("userProduct");

    public final Fream_back.improve_Fream_Back.base.entity.QBaseTimeEntity _super = new Fream_back.improve_Fream_Back.base.entity.QBaseTimeEntity(this);

    public final StringPath condition = createString("condition");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAvailable = createBoolean("isAvailable");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final QProduct product;

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final Fream_back.improve_Fream_Back.user.entity.QUser seller;

    public final NumberPath<java.math.BigDecimal> sellingPrice = createNumber("sellingPrice", java.math.BigDecimal.class);

    public QUserProduct(String variable) {
        this(UserProduct.class, forVariable(variable), INITS);
    }

    public QUserProduct(Path<? extends UserProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserProduct(PathMetadata metadata, PathInits inits) {
        this(UserProduct.class, metadata, inits);
    }

    public QUserProduct(Class<? extends UserProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product")) : null;
        this.seller = inits.isInitialized("seller") ? new Fream_back.improve_Fream_Back.user.entity.QUser(forProperty("seller")) : null;
    }

}

