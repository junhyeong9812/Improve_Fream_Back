package Fream_back.improve_Fream_Back.product.elasticsearch.service;


import Fream_back.improve_Fream_Back.product.elasticsearch.index.ProductColorIndex;
import Fream_back.improve_Fream_Back.product.elasticsearch.repository.ProductColorEsRepository;
import Fream_back.improve_Fream_Back.product.elasticsearch.repository.ProductColorIndexQueryRepository;
import Fream_back.improve_Fream_Back.product.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductColorIndexingService {

    private final ProductColorIndexQueryRepository queryRepository;
    private final ProductColorEsRepository productColorEsRepository;

    @Transactional(readOnly = true)
    public void indexAllColors() {
        // 1) DB에서 ProductColor + 연관 정보 조회
        List<ProductColor> colorList = queryRepository.findAllForIndexing();

        // 2) 변환 (Entity -> Index DTO)
        List<ProductColorIndex> indexList = colorList.stream()
                .map(this::toIndex)
                .collect(Collectors.toList());

        // 3) Elasticsearch에 저장
        productColorEsRepository.saveAll(indexList);
    }

    private ProductColorIndex toIndex(ProductColor pc) {
        Product p = pc.getProduct();
        int minPrice = pc.getSizes().stream()
                .mapToInt(ProductSize::getPurchasePrice)
                .min()
                .orElse(p.getReleasePrice());
        int maxPrice = pc.getSizes().stream()
                .mapToInt(ProductSize::getPurchasePrice)
                .max()
                .orElse(p.getReleasePrice());

        // 사이즈 목록
        List<String> sizes = pc.getSizes().stream()
                .map(ProductSize::getSize)
                .collect(Collectors.toList());

        // 관심 수
        long interestCount = (long) pc.getInterests().size();

        return ProductColorIndex.builder()
                .colorId(pc.getId())
                .productId(p.getId())
                .productName(p.getName())
                .productEnglishName(p.getEnglishName())
                .brandName(p.getBrand() != null ? p.getBrand().getName() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .collectionName(p.getCollection() != null ? p.getCollection().getName() : null)
                .brandId(p.getBrand() != null ? p.getBrand().getId() : null)
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .collectionId(p.getCollection() != null ? p.getCollection().getId() : null)

                .colorName(pc.getColorName())
                .gender(p.getGender().name()) // MALE/FEMALE/KIDS/UNISEX
                .releasePrice(p.getReleasePrice())
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .interestCount(interestCount)

                .sizes(sizes)
                .build();
    }
}