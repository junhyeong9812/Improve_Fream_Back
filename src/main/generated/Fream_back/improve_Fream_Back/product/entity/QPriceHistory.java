package Fream_back.improve_Fream_Back.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPriceHistory is a Querydsl query type for PriceHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPriceHistory extends EntityPathBase<PriceHistory> {

    private static final long serialVersionUID = 1891185598L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPriceHistory priceHistory = new QPriceHistory("priceHistory");

    public final Fream_back.improve_Fream_Back.base.entity.QBaseTimeEntity _super = new Fream_back.improve_Fream_Back.base.entity.QBaseTimeEntity(this);

    public final StringPath changeReason = createString("changeReason");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public final QProduct product;

    public QPriceHistory(String variable) {
        this(PriceHistory.class, forVariable(variable), INITS);
    }

    public QPriceHistory(Path<? extends PriceHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPriceHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPriceHistory(PathMetadata metadata, PathInits inits) {
        this(PriceHistory.class, metadata, inits);
    }

    public QPriceHistory(Class<? extends PriceHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product")) : null;
    }

}

