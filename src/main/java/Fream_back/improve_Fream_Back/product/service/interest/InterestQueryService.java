package Fream_back.improve_Fream_Back.product.service.interest;

import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.repository.InterestQueryDslRepository;
import Fream_back.improve_Fream_Back.product.repository.SortOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestQueryService {

    private final InterestQueryDslRepository interestQueryDslRepository;

    public Page<ProductSearchResponseDto> findUserInterestProducts(
            Long userId,
            SortOption sortoption,
            Pageable pageable) {
        return interestQueryDslRepository.findUserInterestProducts(userId,sortoption, pageable);
    }
}