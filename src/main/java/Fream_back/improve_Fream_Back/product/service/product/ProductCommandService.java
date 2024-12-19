package Fream_back.improve_Fream_Back.product.service.product;

import Fream_back.improve_Fream_Back.product.dto.ProductCreateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.ProductCreateResponseDto;
import Fream_back.improve_Fream_Back.product.dto.ProductUpdateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.ProductUpdateResponseDto;
import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.service.brand.BrandEntityService;
import Fream_back.improve_Fream_Back.product.service.category.CategoryEntityService;
import Fream_back.improve_Fream_Back.product.service.collection.CollectionCommandService;
import Fream_back.improve_Fream_Back.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final BrandEntityService brandEntityService;
    private final CategoryEntityService categoryEntityService;
    private final CollectionCommandService collectionCommandService;


    public ProductCreateResponseDto createProduct(ProductCreateRequestDto request) {
        // 브랜드 엔티티 확인
        Brand brand = brandEntityService.findByName(request.getBrandName());

        // 서브 카테고리 확인
        Category category = categoryEntityService.findSubCategoryByName(
                request.getCategoryName(),
                request.getMainCategoryName()
        );
        // 컬렉션 확인 및 생성
        Collection collection = null;
        if (request.getCollectionName() != null) {
            collection = collectionCommandService.createOrGetCollection(request.getCollectionName());
        }

        // 상품 생성
        Product product = Product.builder()
                .name(request.getName())
                .englishName(request.getEnglishName())
                .releasePrice(request.getReleasePrice())
                .modelNumber(request.getModelNumber())
                .releaseDate(request.getReleaseDate())
                .gender(request.getGender())
                .brand(brand)
                .category(category)
                .collection(collection)
                .build();

        // 저장
        Product savedProduct = productRepository.save(product);

        // DTO 변환 후 반환
        return ProductCreateResponseDto.fromEntity(savedProduct);
    }

    public ProductUpdateResponseDto updateProduct(Long productId, ProductUpdateRequestDto request) {
        // 기존 Product 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 브랜드 확인 (필요시)
        Brand brand = null;
        if (request.getBrandName() != null) {
            brand = brandEntityService.findByName(request.getBrandName());
        }

        // 카테고리 확인 (필요시)
        Category category = null;
        if (request.getCategoryName() != null && request.getMainCategoryName() != null) {
            category = categoryEntityService.findSubCategoryByName(request.getCategoryName(), request.getMainCategoryName());
        }

        // 컬렉션 확인 및 생성
        Collection collection = null;
        if (request.getCollectionName() != null) {
            collection = collectionCommandService.createOrGetCollection(request.getCollectionName());
        }

        // 업데이트 수행
        product.updateProduct(request, brand, category, collection);

        // DTO 변환 후 반환
        return ProductUpdateResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .englishName(product.getEnglishName())
                .releasePrice(product.getReleasePrice())
                .modelNumber(product.getModelNumber())
                .releaseDate(product.getReleaseDate())
                .gender(request.getGender())
                .brandName(product.getBrand().getName())
                .categoryName(product.getCategory().getName())
                .collectionName(product.getCollection() != null ? product.getCollection().getName() : null)
                .build();
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        productRepository.delete(product);
    }
}
