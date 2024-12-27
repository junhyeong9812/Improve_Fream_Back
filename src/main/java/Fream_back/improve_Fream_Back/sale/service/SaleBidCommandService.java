package Fream_back.improve_Fream_Back.sale.service;

import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.product.service.productSize.ProductSizeQueryService;
import Fream_back.improve_Fream_Back.sale.entity.BidStatus;
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

    @Transactional
    public SaleBid createSaleBid(String email, Long productSizeId, int bidPrice) {
        User seller = userQueryService.findByEmail(email); // 이메일로 유저 조회
        ProductSize productSize = productSizeQueryService.findById(productSizeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사이즈를 찾을 수 없습니다."));

        SaleBid saleBid = SaleBid.builder()
                .seller(seller)
                .productSize(productSize)
                .bidPrice(bidPrice)
                .status(BidStatus.PENDING)
                .build();

        return saleBidRepository.save(saleBid);
    }
}
