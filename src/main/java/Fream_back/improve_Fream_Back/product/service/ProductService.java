package Fream_back.improve_Fream_Back.product.service;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.ClothingSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.ShoeSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import Fream_back.improve_Fream_Back.product.entity.size.ProductSizeAndColorQuantity;
import Fream_back.improve_Fream_Back.product.repository.*;
import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import Fream_back.improve_Fream_Back.Category.repository.MainCategoryRepository;
import Fream_back.improve_Fream_Back.Category.repository.SubCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductSizeAndColorQuantityRepository productSizeAndColorQuantityRepository;
    private final ProductQueryRepository productQueryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          MainCategoryRepository mainCategoryRepository,
                          SubCategoryRepository subCategoryRepository,
                          ProductImageRepository productImageRepository,
                          ProductSizeAndColorQuantityRepository productSizeAndColorQuantityRepository,
                          ProductQueryRepository productQueryRepository) {
        this.productRepository = productRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.productImageRepository = productImageRepository;
        this.productSizeAndColorQuantityRepository = productSizeAndColorQuantityRepository;
        this.productQueryRepository = productQueryRepository;
    }

    // 상품 생성
    public Product createProduct(ProductCreateRequestDto productDto) {
        MainCategory mainCategory = mainCategoryRepository.findById(productDto.getMainCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("MainCategory not found"));
        SubCategory subCategory = subCategoryRepository.findById(productDto.getSubCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("SubCategory not found"));

        Product product = Product.builder()
                .name(productDto.getName())
                .brand(productDto.getBrand())
                .sku(productDto.getSku())
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .initialPrice(productDto.getInitialPrice())
                .description(productDto.getDescription())
                .build();

        product = productRepository.save(product);

        // 상품 이미지 추가
        if (productDto.getImages() != null) {
            Set<ProductImage> productImages = productDto.getImages().stream().map(imageDto -> {
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(imageDto.getImageUrl())
                        .imageType(imageDto.getImageType())
                        .isMainThumbnail(imageDto.isMainThumbnail())
                        .build();
                productImage.assignProduct(product);
                return productImage;
            }).collect(Collectors.toSet());
            productImageRepository.saveAll(productImages);
        }

        // 사이즈 및 색상별 수량 추가
        if (productDto.getSizeAndColorQuantities() != null) {
            Set<ProductSizeAndColorQuantity> sizeAndColorQuantities = productDto.getSizeAndColorQuantities().stream().map(sizeAndColorDto -> {
                SizeType sizeType = SizeType.valueOf(sizeAndColorDto.getSizeType());

                ProductSizeAndColorQuantity.ProductSizeAndColorQuantityBuilder builder = ProductSizeAndColorQuantity.builder()
                        .sizeType(sizeType)
                        .color(sizeAndColorDto.getColor())
                        .quantity(sizeAndColorDto.getQuantity());

                // 사이즈 타입에 따라 옷 사이즈 또는 신발 사이즈 설정
                if (sizeType == SizeType.CLOTHING) {
                    builder.clothingSize(ClothingSizeType.valueOf(sizeAndColorDto.getClothingSize()));
                } else if (sizeType == SizeType.SHOES) {
                    builder.shoeSize(ShoeSizeType.valueOf(sizeAndColorDto.getShoeSize()));
                }

                ProductSizeAndColorQuantity sizeAndColorQuantity = builder.build();
                sizeAndColorQuantity.assignProduct(product);
                return sizeAndColorQuantity;
            }).collect(Collectors.toSet());
            productSizeAndColorQuantityRepository.saveAll(sizeAndColorQuantities);
        }

        return product;
    }

    // 상품 수정
    public Product updateProduct(Long productId, ProductUpdateRequestDto productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // 상품 수정 메서드 호출
        MainCategory mainCategory = mainCategoryRepository.findById(productDto.getMainCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("MainCategory not found"));
        SubCategory subCategory = subCategoryRepository.findById(productDto.getSubCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("SubCategory not found"));

        product.updateProductInfo(productDto.getName(), productDto.getBrand(), productDto.getSku(), mainCategory, subCategory, productDto.getInitialPrice(), productDto.getDescription());

        // 이미지 수정
        productImageRepository.deleteAll(productImageRepository.findAllByProductId(productId));
        if (productDto.getImages() != null) {
            Set<ProductImage> productImages = productDto.getImages().stream().map(imageDto -> {
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(imageDto.getImageUrl())
                        .imageType(imageDto.getImageType())
                        .isMainThumbnail(imageDto.isMainThumbnail())
                        .build();
                productImage.assignProduct(product);
                return productImage;
            }).collect(Collectors.toSet());
            productImageRepository.saveAll(productImages);
        }

        // 사이즈 및 색상별 수량 수정
        productSizeAndColorQuantityRepository.deleteAll(productSizeAndColorQuantityRepository.findAllByProductId(productId));
        if (productDto.getSizeAndColorQuantities() != null) {
            Set<ProductSizeAndColorQuantity> sizeAndColorQuantities = productDto.getSizeAndColorQuantities().stream().map(sizeAndColorDto -> {
                SizeType sizeType = SizeType.valueOf(sizeAndColorDto.getSizeType());

                ProductSizeAndColorQuantity.ProductSizeAndColorQuantityBuilder builder = ProductSizeAndColorQuantity.builder()
                        .sizeType(sizeType)
                        .color(sizeAndColorDto.getColor())
                        .quantity(sizeAndColorDto.getQuantity());

                // 사이즈 타입에 따라 옷 사이즈 또는 신발 사이즈 설정
                if (sizeType == SizeType.CLOTHING) {
                    builder.clothingSize(ClothingSizeType.valueOf(sizeAndColorDto.getClothingSize()));
                } else if (sizeType == SizeType.SHOES) {
                    builder.shoeSize(ShoeSizeType.valueOf(sizeAndColorDto.getShoeSize()));
                }

                ProductSizeAndColorQuantity sizeAndColorQuantity = builder.build();
                sizeAndColorQuantity.assignProduct(product);
                return sizeAndColorQuantity;
            }).collect(Collectors.toSet());
            productSizeAndColorQuantityRepository.saveAll(sizeAndColorQuantities);
        }

        return productRepository.save(product);
    }

    // 상품 삭제
    public void deleteProduct(ProductDeleteRequestDto productDeleteRequestDto) {
        Long productId = productDeleteRequestDto.getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    // 필터링 조회 (QueryDSL)
    public List<ProductQueryDslResponseDto> searchProducts(ProductQueryDslRequestDto queryDslRequestDto) {
        return productQueryRepository.findProductsByFilter(
                queryDslRequestDto.getMainCategoryId(),
                queryDslRequestDto.getSubCategoryId(),
                queryDslRequestDto.getColor(),
                queryDslRequestDto.getSize());
    }
}
