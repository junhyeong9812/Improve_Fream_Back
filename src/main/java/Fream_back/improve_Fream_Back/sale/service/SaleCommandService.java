package Fream_back.improve_Fream_Back.sale.service;

import Fream_back.improve_Fream_Back.notification.entity.NotificationCategory;
import Fream_back.improve_Fream_Back.notification.entity.NotificationType;
import Fream_back.improve_Fream_Back.notification.service.NotificationCommandService;
import Fream_back.improve_Fream_Back.order.entity.OrderBid;
import Fream_back.improve_Fream_Back.order.service.OrderBidQueryService;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.sale.entity.*;
import Fream_back.improve_Fream_Back.sale.repository.SaleBidRepository;
import Fream_back.improve_Fream_Back.sale.repository.SaleRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleCommandService {

    private final SaleRepository saleRepository;
    private final OrderBidQueryService orderBidQueryService;
    private final NotificationCommandService notificationCommandService;
    private final UserQueryService userQueryService;

    @Transactional
    public Sale createInstantSale(
            Long orderBidId,
            String sellerEmail,
            String returnAddress,
            String postalCode,
            String receiverPhone,
            SaleBankAccount saleBankAccount
    ) {
        // 1. OrderBid 조회
        OrderBid orderBid = orderBidQueryService.findById(orderBidId)
                .orElseThrow(() -> new IllegalArgumentException("해당 OrderBid를 찾을 수 없습니다."));

        // 2. 판매자(User) 조회
        User seller = userQueryService.findByEmail(sellerEmail);

        // 3. ProductSize 정보 가져오기
        ProductSize productSize = orderBid.getProductSize();

        // 4. Sale 생성
        Sale sale = Sale.builder()
                .seller(seller)
                .productSize(productSize)
                .returnAddress(returnAddress)
                .postalCode(postalCode)
                .receiverPhone(receiverPhone)
                .saleBankAccount(saleBankAccount)
                .status(SaleStatus.PENDING_SHIPMENT) // 판매자 발송 대기 상태
                .build();

        Sale savedSale = saleRepository.save(sale);

        // 5. OrderBid와 Sale 연결
        orderBid.assignSale(savedSale);

        // 6. 알림 전송
        User buyer = orderBid.getUser();
        notificationCommandService.createNotification(
                buyer.getId(),
                NotificationCategory.SHOPPING,
                NotificationType.BID,
                "구매 입찰에 판매자가 등록되었습니다. 확인해보세요!"
        );

        return savedSale;
    }

    @Transactional
    public void updateSaleStatus(Long saleId, SaleStatus newStatus) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Sale을 찾을 수 없습니다: " + saleId));

        if (!sale.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException("Sale 상태를 " + newStatus + "로 전환할 수 없습니다.");
        }

        sale.updateStatus(newStatus);
    }


}


