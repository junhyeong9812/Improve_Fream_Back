package Fream_back.improve_Fream_Back.product.elasticsearch.repository;


import Fream_back.improve_Fream_Back.product.elasticsearch.index.ProductColorIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductColorEsRepository extends ElasticsearchRepository<ProductColorIndex, Long> {
    // 필요하면 추가 메서드 선언
}