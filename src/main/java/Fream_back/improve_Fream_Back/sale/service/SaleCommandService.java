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
import Fream_back.improve_Fream_Back.shipment.entity.SellerShipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import Fream_back.improve_Fream_Back.shipment.service.SellerShipmentCommandService;
import Fream_back.improve_Fream_Back.user.dto.BankAccount.BankAccountInfoDto;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import Fream_back.improve_Fream_Back.user.service.bankaccount.BankAccountQueryService;
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
    private final SellerShipmentCommandService sellerShipmentCommandService;
    private final BankAccountQueryService bankAccountQueryService;
    private final SaleBankAccountCommandService saleBankAccountCommandService;


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
    public Sale createSale(User seller, ProductSize productSize, String returnAddress,
                           String postalCode, String receiverPhone, boolean isWarehouseStorage) {
        // 1. BankAccount 정보 조회
        BankAccountInfoDto bankAccountInfo = bankAccountQueryService.getBankAccount(seller.getEmail());

        // 2. Sale 생성
        Sale sale = Sale.builder()
                .seller(seller)
                .productSize(productSize)
                .returnAddress(returnAddress)
                .postalCode(postalCode)
                .receiverPhone(receiverPhone)
                .isWarehouseStorage(isWarehouseStorage) // 창고 보관 여부 설정
                .status(SaleStatus.PENDING_SHIPMENT) // 초기 상태 설정
                .build();

        Sale savedSale = saleRepository.save(sale);

        // 3. SaleBankAccount 생성 및 저장
        SaleBankAccount saleBankAccount = saleBankAccountCommandService.createSaleBankAccount(
                bankAccountInfo.getBankName(),
                bankAccountInfo.getAccountNumber(),
                bankAccountInfo.getAccountHolder(),
                savedSale
        );

        // 4. Sale에 SaleBankAccount 연관 설정
        savedSale.assignSaleBankAccount(saleBankAccount);

        // 5. 알림 전송
        notificationCommandService.createNotification(
                seller.getId(),
                NotificationCategory.SHOPPING,
                NotificationType.BID,
                "판매 입찰이 등록되었습니다."
        );

        return savedSale;
    }


    @Transactional
    public SellerShipment createSellerShipment(Long saleId, String courier, String trackingNumber) {
        // 1. SellerShipment 생성
        SellerShipment shipment = sellerShipmentCommandService.createSellerShipment(saleId, courier, trackingNumber);

        // 2. Sale 상태를 배송 중으로 업데이트
        updateSaleStatus(saleId, SaleStatus.IN_TRANSIT);

        return shipment;
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


