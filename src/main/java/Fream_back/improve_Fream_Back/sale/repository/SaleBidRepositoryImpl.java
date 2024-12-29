package Fream_back.improve_Fream_Back.sale.repository;

import Fream_back.improve_Fream_Back.sale.dto.SaleBidStatusCountDto;
import Fream_back.improve_Fream_Back.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import Fream_back.improve_Fream_Back.sale.dto.SaleBidResponseDto;
import Fream_back.improve_Fream_Back.sale.entity.QSale;
import Fream_back.improve_Fream_Back.sale.entity.QSaleBid;
import Fream_back.improve_Fream_Back.product.entity.QProduct;
import Fream_back.improve_Fream_Back.product.entity.QProductColor;
import Fream_back.improve_Fream_Back.product.entity.QProductImage;
import Fream_back.improve_Fream_Back.product.entity.QProductSize;
import Fream_back.improve_Fream_Back.shipment.entity.QSellerShipment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SaleBidRepositoryImpl implements SaleBidRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SaleBidResponseDto> findSaleBidsByFilters(String email, String saleBidStatus, String saleStatus, Pageable pageable) {
        QSaleBid saleBid = QSaleBid.saleBid;
        QSale sale = QSale.sale;
        QProductSize productSize = QProductSize.productSize;
        QProductColor productColor = QProductColor.productColor;
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        QSellerShipment sellerShipment = QSellerShipment.sellerShipment;
//        QUser user = QUser.user; // 유저 엔티티


        // Main Query
        List<SaleBidResponseDto> content = queryFactory
                .select(Projections.constructor(
                        SaleBidResponseDto.class,
                        saleBid.id,
                        product.id,
                        product.name,
                        product.englishName,
                        productSize.size,
                        productColor.colorName,
                        productImage.imageUrl,
                        saleBid.bidPrice,
                        saleBid.status.stringValue(),
                        sale.status.stringValue(),
                        sellerShipment.status.stringValue(),
                        saleBid.createdDate,
                        saleBid.modifiedDate
                ))
                .from(saleBid)
//                .join(saleBid.seller, user).on(user.email.eq(email)) // 유저 조인 및 이메일 필터링
                .join(saleBid.seller).on(saleBid.seller.email.eq(email)) // 이메일 조건
                .join(saleBid.productSize, productSize)
                .join(productSize.productColor, productColor)
                .join(productColor.product, product)
                .join(productColor.thumbnailImage, productImage)
                .leftJoin(saleBid.sale, sale)
                .leftJoin(sale.sellerShipment, sellerShipment)
                .where(
                        saleBidStatus != null ? saleBid.status.stringValue().eq(saleBidStatus) : null,
                        saleStatus != null ? sale.status.stringValue().eq(saleStatus) : null
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count Query
        long total = queryFactory
                .select(saleBid.id.count())
                .from(saleBid)
                .join(saleBid.seller).on(saleBid.seller.email.eq(email)) // 이메일 조건
                .leftJoin(saleBid.sale, sale)
                .where(
                        saleBidStatus != null ? saleBid.status.stringValue().eq(saleBidStatus) : null,
                        saleStatus != null ? sale.status.stringValue().eq(saleStatus) : null
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public SaleBidStatusCountDto countSaleBidsByStatus(String email) {
        QSaleBid saleBid = QSaleBid.saleBid;
        QUser user = QUser.user;

        long pendingCount = queryFactory
                .select(saleBid.count())
                .from(saleBid)
                .where(saleBid.status.eq(Fream_back.improve_Fream_Back.sale.entity.BidStatus.PENDING)
                        .and(saleBid.seller.email.eq(email)))
                .fetchOne();

        long matchedCount = queryFactory
                .select(saleBid.count())
                .from(saleBid)
                .where(saleBid.status.eq(Fream_back.improve_Fream_Back.sale.entity.BidStatus.MATCHED)
                        .and(saleBid.seller.email.eq(email)))
                .fetchOne();

        long cancelledOrCompletedCount = queryFactory
                .select(saleBid.count())
                .from(saleBid)
                .where(saleBid.status.in(
                                Fream_back.improve_Fream_Back.sale.entity.BidStatus.CANCELLED,
                                Fream_back.improve_Fream_Back.sale.entity.BidStatus.COMPLETED)
                        .and(saleBid.seller.email.eq(email)))
                .fetchOne();

        return new SaleBidStatusCountDto(pendingCount, matchedCount, cancelledOrCompletedCount);
    }
}
