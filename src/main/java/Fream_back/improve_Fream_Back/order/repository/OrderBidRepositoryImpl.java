package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.dto.OrderBidStatusCountDto;
import Fream_back.improve_Fream_Back.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import Fream_back.improve_Fream_Back.order.dto.OrderBidResponseDto;
import Fream_back.improve_Fream_Back.order.entity.QOrder;
import Fream_back.improve_Fream_Back.order.entity.QOrderBid;
import Fream_back.improve_Fream_Back.product.entity.QProduct;
import Fream_back.improve_Fream_Back.product.entity.QProductColor;
import Fream_back.improve_Fream_Back.product.entity.QProductImage;
import Fream_back.improve_Fream_Back.product.entity.QProductSize;
import Fream_back.improve_Fream_Back.shipment.entity.QOrderShipment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderBidRepositoryImpl implements OrderBidRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderBidResponseDto> findOrderBidsByFilters(String email, String bidStatus, String orderStatus, Pageable pageable) {
        QOrderBid orderBid = QOrderBid.orderBid;
        QOrder order = QOrder.order;
        QProductSize productSize = QProductSize.productSize;
        QProductColor productColor = QProductColor.productColor;
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        QOrderShipment orderShipment = QOrderShipment.orderShipment;
        QUser user = QUser.user;

        // Main Query
        List<OrderBidResponseDto> content = queryFactory
                .select(Projections.constructor(
                        OrderBidResponseDto.class,
                        orderBid.id,
                        product.id,
                        product.name,
                        product.englishName,
                        productSize.size,
                        productColor.colorName,
                        productImage.imageUrl,
                        orderBid.bidPrice,
                        orderBid.status.stringValue(),
                        order.status.stringValue(),
                        orderShipment.status.stringValue(),
                        orderBid.createdDate,
                        orderBid.modifiedDate
                ))
                .from(orderBid)
                .join(orderBid.user, user) // User 엔티티 조인
                .join(orderBid.productSize, productSize)
                .join(productSize.productColor, productColor)
                .join(productColor.product, product)
                .join(productColor.thumbnailImage, productImage)
                .leftJoin(orderBid.order, order)
                .leftJoin(order.orderShipment, orderShipment)
                .where(
                        email != null ? user.email.eq(email) : null, // 이메일 조건
                        bidStatus != null ? orderBid.status.stringValue().eq(bidStatus) : null,
                        orderStatus != null ? order.status.stringValue().eq(orderStatus) : null
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count Query
        long total = queryFactory
                .select(orderBid.id.count())
                .from(orderBid)
                .join(orderBid.user, user) // User 엔티티 조인
                .leftJoin(orderBid.order, order)
                .where(
                        email != null ? user.email.eq(email) : null, // 이메일 조건
                        bidStatus != null ? orderBid.status.stringValue().eq(bidStatus) : null,
                        orderStatus != null ? order.status.stringValue().eq(orderStatus) : null
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public OrderBidStatusCountDto countOrderBidsByStatus(String email) {
        QOrderBid orderBid = QOrderBid.orderBid;
        QUser user = QUser.user;

        long pendingCount = queryFactory
                .select(orderBid.count())
                .from(orderBid)
                .where(orderBid.status.eq(Fream_back.improve_Fream_Back.order.entity.BidStatus.PENDING)
                        .and(orderBid.user.email.eq(email)))
                .fetchOne();

        long matchedCount = queryFactory
                .select(orderBid.count())
                .from(orderBid)
                .where(orderBid.status.eq(Fream_back.improve_Fream_Back.order.entity.BidStatus.MATCHED)
                        .and(orderBid.user.email.eq(email)))
                .fetchOne();

        long cancelledOrCompletedCount = queryFactory
                .select(orderBid.count())
                .from(orderBid)
                .where(orderBid.status.in(
                                Fream_back.improve_Fream_Back.order.entity.BidStatus.CANCELLED,
                                Fream_back.improve_Fream_Back.order.entity.BidStatus.COMPLETED)
                        .and(orderBid.user.email.eq(email)))
                .fetchOne();

        return new OrderBidStatusCountDto(pendingCount, matchedCount, cancelledOrCompletedCount);
    }
}
