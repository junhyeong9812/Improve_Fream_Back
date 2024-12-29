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
    @Transactional
    public void deleteSaleBid(Long saleBidId) {
        // SaleBid 조회
        SaleBid saleBid = saleBidRepository.findById(saleBidId)
                .orElseThrow(() -> new IllegalArgumentException("해당 SaleBid를 찾을 수 없습니다: " + saleBidId));

        // Order와 연결 여부 확인
        if (saleBid.getOrder() != null) {
            throw new IllegalStateException("SaleBid는 Order와 연결되어 있으므로 삭제할 수 없습니다.");
        }

        // Sale과 연결 여부 확인 후 삭제
        if (saleBid.getSale() != null) {
            saleCommandService.deleteSale(saleBid.getSale().getId());
        }

        // SaleBid 삭제
        saleBidRepository.delete(saleBid);
    }
}
