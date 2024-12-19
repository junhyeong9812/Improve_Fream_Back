package Fream_back.improve_Fream_Back.product.service.Interest;

import Fream_back.improve_Fream_Back.product.entity.Interest;
import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.repository.InterestRepository;
import Fream_back.improve_Fream_Back.product.service.productColor.ProductColorCommandService;
import Fream_back.improve_Fream_Back.product.service.productColor.ProductColorQueryService;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InterestCommandService {

    private final InterestRepository interestRepository;
    private final UserQueryService userQueryService;
    private final ProductColorQueryService productColorQueryService;
    // 관심 상품 토글 (추가/삭제)
    public void toggleInterest(String userEmail, Long productColorId) {
        User user = userQueryService.findByEmail(userEmail); // 이메일로 유저 조회
        ProductColor productColor = productColorQueryService.findById(productColorId); // ProductColor 조회

        // 관심 상품이 이미 등록되어 있는지 확인
        interestRepository.findByUserAndProductColor(user, productColor).ifPresentOrElse(
                interest -> {
                    // 이미 등록된 경우 삭제
                    interestRepository.delete(interest);
                },
                () -> {
                    // 등록되지 않은 경우 추가
                    Interest newInterest = Interest.builder()
                            .user(user)
                            .productColor(productColor)
                            .build();
                    interestRepository.save(newInterest);
                }
        );
    }
}