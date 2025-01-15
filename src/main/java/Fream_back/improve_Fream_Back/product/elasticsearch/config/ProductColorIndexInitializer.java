package Fream_back.improve_Fream_Back.product.elasticsearch.config;

import Fream_back.improve_Fream_Back.product.elasticsearch.service.ProductColorIndexingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
public class ProductColorIndexInitializer implements CommandLineRunner {

    private final ProductColorIndexingService indexingService;

//    @PostConstruct
//    public void initIndex() {
//        indexingService.indexAllColors();
//    }
@Override
public void run(String... args) {
    // 2) 모든 RDB 데이터 준비된 뒤 → ES 인덱싱
    indexingService.indexAllColors();
}
}
