package Fream_back.improve_Fream_Back.sale.service;

import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.product.service.productSize.ProductSizeQueryService;
import Fream_back.improve_Fream_Back.sale.entity.BidStatus;
import Fream_back.improve_Fream_Back.sale.entity.Sale;
import Fream_back.improve_Fream_Back.sale.entity.SaleBid;
import Fream_back.improve_Fream_Back.sale.repository.SaleBidRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleBidCommandService {

    private final SaleBidRepository saleBidRepository;
    private final UserQueryService userQueryService;
    private final ProductSizeQueryService productSizeQueryService;
    private final SaleCommandService saleCommandService;

    @Transactional
    public SaleBid createSaleBid(String sellerEmail, Long productSizeId, int bidPrice,
                                 String returnAddress, String postalCode, String receiverPhone
                                ,boolean isWarehouseStorage) {
        // 1. User 조회
        User seller = userQueryService.findByEmail(sellerEmail);

        // 2. ProductSize 조회
        ProductSize productSize = productSizeQueryService.findById(productSizeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품 사이즈를 찾을 수 없습니다: " + productSizeId));

        // 3. Sale 생성
        Sale sale = saleCommandService.createSale(seller, productSize, returnAddress, postalCode, receiverPhone,isWarehouseStorage);

        // 4. SaleBid 생성
        SaleBid saleBid = SaleBid.builder()
                .seller(seller)
                .productSize(productSize)
                .bidPrice(bidPrice)
                .status(BidStatus.PENDING)
                .sale(sale) // 연관된 Sale 설정
                .build();

        return saleBidRepository.save(saleBid);
    }
}
