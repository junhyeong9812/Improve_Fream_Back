package Fream_back.improve_Fream_Back.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 774859756L;

    public static final QUser user = new QUser("user");

    public final Fream_back.improve_Fream_Back.base.entity.QBaseEntity _super = new Fream_back.improve_Fream_Back.base.entity.QBaseEntity(this);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final BooleanPath emailNotificationConsent = createBoolean("emailNotificationConsent");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath loginId = createString("loginId");

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final BooleanPath phoneNotificationConsent = createBoolean("phoneNotificationConsent");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final ListPath<Fream_back.improve_Fream_Back.product.entity.UserProduct, Fream_back.improve_Fream_Back.product.entity.QUserProduct> productsForSale = this.<Fream_back.improve_Fream_Back.product.entity.UserProduct, Fream_back.improve_Fream_Back.product.entity.QUserProduct>createList("productsForSale", Fream_back.improve_Fream_Back.product.entity.UserProduct.class, Fream_back.improve_Fream_Back.product.entity.QUserProduct.class, PathInits.DIRECT2);

    public final StringPath realName = createString("realName");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

