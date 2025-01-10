package Fream_back.improve_Fream_Back.product.elasticsearch.config;

import Fream_back.improve_Fream_Back.product.elasticsearch.service.ProductColorIndexingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductColorIndexInitializer {

    private final ProductColorIndexingService indexingService;

    @PostConstruct
    public void initIndex() {
        indexingService.indexAllColors();
    }
}
