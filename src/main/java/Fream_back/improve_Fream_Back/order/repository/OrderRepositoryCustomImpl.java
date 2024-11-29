package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.QOrder;
import Fream_back.improve_Fream_Back.payment.entity.QPayment;
import Fream_back.improve_Fream_Back.shipment.entity.QShipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Order> findOrdersWithDynamicFilters(Long userId, ShipmentStatus shipmentStatus, boolean includePayments) {
        QOrder order = QOrder.order;
        QShipment shipment = QShipment.shipment;
        QPayment payment = QPayment.payment;

        var query = queryFactory.selectFrom(order)
                .leftJoin(order.shipment, shipment).fetchJoin()
                .leftJoin(order.orderItems).fetchJoin()
                .where(order.user.id.eq(userId));

        // 배송 상태 조건 추가
        if (shipmentStatus != null) {
            query.where(shipment.shipmentStatus.eq(shipmentStatus));
        }

        // 결제 내역 포함 여부
        if (includePayments) {
            query.leftJoin(order.payment, payment).fetchJoin();
        }

        return query.fetch();
    }
}