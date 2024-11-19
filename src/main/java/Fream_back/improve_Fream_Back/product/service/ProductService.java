package Fream_back.improve_Fream_Back.product.service;

import Fream_back.improve_Fream_Back.Category.dto.MainCategoryDto;
import Fream_back.improve_Fream_Back.Category.dto.SubCategoryDto;
import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.ClothingSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.Color;
import Fream_back.improve_Fream_Back.product.entity.enumType.ShoeSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import Fream_back.improve_Fream_Back.product.entity.size.ProductSizeAndColorQuantity;
import Fream_back.improve_Fream_Back.product.repository.*;
import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import Fream_back.improve_Fream_Back.Category.repository.MainCategoryRepository;
import Fream_back.improve_Fream_Back.Category.repository.SubCategoryRepository;
import Fream_back.improve_Fream_Back.product.service.fileStorageUtil.FileStorageUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductSizeAndColorQuantityRepository productSizeAndColorQuantityRepository;
    private final ProductQueryRepository productQueryRepository;
    private final FileStorageUtil fileStorageUtil;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          MainCategoryRepository mainCategoryRepository,
                          SubCategoryRepository subCategoryRepository,
                          ProductImageRepository productImageRepository,
                          ProductSizeAndColorQuantityRepository productSizeAndColorQuantityRepository,
                          ProductQueryRepository productQueryRepository,
                          FileStorageUtil fileStorageUtil) {
        this.productRepository = productRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.productImageRepository = productImageRepository;
        this.productSizeAndColorQuantityRepository = productSizeAndColorQuantityRepository;
        this.productQueryRepository = productQueryRepository;
        this.fileStorageUtil = fileStorageUtil;
    }

    /**
     * 임시 URL 생성 메서드
     *
     * @param file 업로드할 이미지 파일
     * @return 생성된 임시 URL
     */
    public String createTemporaryUrl(MultipartFile file) {
        try {
            return fileStorageUtil.saveTemporaryFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save temporary file", e);
        }
    }

    /**
     * 상품 생성 메서드
     *
     * @param productDto    생성할 상품의 상세 정보 DTO
     * @param tempFilePaths 임시 저장된 이미지 파일 경로 목록
     * @return 생성된 상품의 ID를 담은 DTO
     */
    public ProductIdResponseDto createProduct(ProductCreateRequestDto productDto, List<String> tempFilePaths) {
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
                .releaseDate(productDto.getReleaseDate())
                .build();

        productRepository.save(product);

        // **색상과 사이즈 데이터 저장**
        for (ProductSizeAndColorQuantityDto quantityDto : productDto.getSizeAndColorQuantities()) {
            for (String color : quantityDto.getColors()) {
                // **의류 사이즈 처리**
                if (quantityDto.getClothingSizes() != null && !quantityDto.getClothingSizes().isEmpty()) {
                    for (String clothingSize : quantityDto.getClothingSizes()) {
                        ProductSizeAndColorQuantity quantity = ProductSizeAndColorQuantity.builder()
                                .product(product)
                                .sizeType(SizeType.CLOTHING)
                                .clothingSize(ClothingSizeType.valueOf(clothingSize))
                                .color(Color.valueOf(color))
                                .quantity(quantityDto.getQuantity())
                                .build();
                        product.addSizeAndColorQuantity(quantity); // 연관관계 편의 메서드 사용
                    }
                }

                // **신발 사이즈 처리**
                if (quantityDto.getShoeSizes() != null && !quantityDto.getShoeSizes().isEmpty()) {
                    for (String shoeSize : quantityDto.getShoeSizes()) {
                        ProductSizeAndColorQuantity quantity = ProductSizeAndColorQuantity.builder()
                                .product(product)
                                .sizeType(SizeType.SHOES)
                                .shoeSize(ShoeSizeType.valueOf(shoeSize))
                                .color(Color.valueOf(color))
                                .quantity(quantityDto.getQuantity())
                                .build();
                        product.addSizeAndColorQuantity(quantity); // 연관관계 편의 메서드 사용
                    }
                }
            }
        }

        // 임시 파일을 최종 경로로 이동하여 저장
        tempFilePaths.forEach(tempFilePath -> {
            try {
                String permanentPath = fileStorageUtil.moveToPermanentStorage(tempFilePath, product.getId());
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(permanentPath)
                        .imageType("detail")
                        .isMainThumbnail(false)
                        .build();
                productImage.assignProduct(product);
                productImageRepository.save(productImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to move file to permanent storage", e);
            }
        });

        return new ProductIdResponseDto(product.getId());
    }

    /**
     * 상품 수정 메서드
     *
     * @param productId     수정할 상품 ID
     * @param productDto    수정할 상품의 상세 정보 DTO
     * @param tempFilePaths 임시 저장된 이미지 파일 경로 목록
     * @return 수정된 상품의 ID를 담은 DTO
     */
    public ProductIdResponseDto updateProduct(Long productId, ProductUpdateRequestDto productDto, List<String> tempFilePaths) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        MainCategory mainCategory = mainCategoryRepository.findById(productDto.getMainCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("MainCategory not found"));
        SubCategory subCategory = subCategoryRepository.findById(productDto.getSubCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("SubCategory not found"));

        product.updateProductInfo(
                productDto.getName(),
                productDto.getBrand(),
                productDto.getSku(),
                mainCategory,
                subCategory,
                productDto.getInitialPrice(),
                productDto.getDescription(),
                productDto.getReleaseDate()
        );

        // **기존 사이즈 및 색상 조합 처리**
        Set<ProductSizeAndColorQuantity> existingQuantities = product.getSizeAndColorQuantities();

        // 요청된 새로운 데이터로 변환
        Set<ProductSizeAndColorQuantity> newQuantities = productDto.getSizeAndColorQuantities().stream()
                .flatMap(quantityDto -> {
                    // 의류 사이즈 처리
                    if (quantityDto.getClothingSizes() != null && !quantityDto.getClothingSizes().isEmpty()) {
                        return quantityDto.getColors().stream().flatMap(color ->
                                quantityDto.getClothingSizes().stream().map(clothingSize -> ProductSizeAndColorQuantity.builder()
                                        .product(product)
                                        .sizeType(SizeType.CLOTHING)
                                        .clothingSize(ClothingSizeType.valueOf(clothingSize))
                                        .color(Color.valueOf(color))
                                        .quantity(quantityDto.getQuantity())
                                        .build()));
                    }
                    // 신발 사이즈 처리
                    else if (quantityDto.getShoeSizes() != null && !quantityDto.getShoeSizes().isEmpty()) {
                        return quantityDto.getColors().stream().flatMap(color ->
                                quantityDto.getShoeSizes().stream().map(shoeSize -> ProductSizeAndColorQuantity.builder()
                                        .product(product)
                                        .sizeType(SizeType.SHOES)
                                        .shoeSize(ShoeSizeType.valueOf(shoeSize))
                                        .color(Color.valueOf(color))
                                        .quantity(quantityDto.getQuantity())
                                        .build()));
                    }
                    // 유효하지 않은 데이터는 제외
                    return Stream.empty();
                })
                .collect(Collectors.toSet());

        // 삭제: 기존 데이터 중 요청에 없는 데이터 삭제
        existingQuantities.stream()
                .filter(existing -> !newQuantities.contains(existing))
                .forEach(productSizeAndColorQuantityRepository::delete);

        // 추가: 새로운 데이터 중 기존에 없는 데이터 추가
        newQuantities.stream()
                .filter(newQuantity -> !existingQuantities.contains(newQuantity))
                .forEach(product::addSizeAndColorQuantity);

        // 기존 이미지 처리
        Set<String> updatedImageUrls = productDto.getImages().stream()
                .map(ProductImageDto::getImageUrl)
                .collect(Collectors.toSet());

        List<ProductImage> existingImages = productImageRepository.findAllByProductId(productId);
        existingImages.forEach(existingImage -> {
            if (!updatedImageUrls.contains(existingImage.getImageUrl())) {
                try {
                    fileStorageUtil.deleteFile(existingImage.getImageUrl());
                    productImageRepository.delete(existingImage);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete image file", e);
                }
            }
        });

        // 새로운 이미지 추가
        tempFilePaths.forEach(tempFilePath -> {
            try {
                String permanentPath = fileStorageUtil.moveToPermanentStorage(tempFilePath, productId);
                ProductImage newProductImage = ProductImage.builder()
                        .imageUrl(permanentPath)
                        .imageType("detail")
                        .isMainThumbnail(false)
                        .build();
                newProductImage.assignProduct(product);
                productImageRepository.save(newProductImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save new image", e);
            }
        });

        return new ProductIdResponseDto(product.getId());
    }

    /**
     * 상품 삭제 메서드
     *
     * @param productDeleteRequestDto 삭제할 상품의 ID를 담은 DTO
     */
    public String deleteProduct(ProductDeleteRequestDto productDeleteRequestDto) {
        Long productId = productDeleteRequestDto.getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // 이미지 파일 삭제
        List<ProductImage> images = productImageRepository.findAllByProductId(productId);
        images.forEach(image -> {
            try {
                fileStorageUtil.deleteFile(image.getImageUrl());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image file", e);
            }
        });

        productRepository.delete(product);
        return "Product deleted successfully.";
    }

    /**
     * 단일 상품 조회 메서드
     *
     * @param productId 조회할 상품 ID
     * @return 단일 상품의 상세 정보를 담은 DTO
     */
    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findByIdWithDetails(productId);

        if (product == null) {
            throw new EntityNotFoundException("Product not found");
        }

        // 이미지 리스트 생성
        List<ProductImageDto> images = productImageRepository.findAllByProductId(productId).stream()
                .map(image -> ProductImageDto.builder()
                        .id(image.getId())
                        .imageUrl(image.getImageUrl())
                        .imageType(image.getImageType())
                        .isMainThumbnail(image.isMainThumbnail())
                        .build())
                .collect(Collectors.toList());

        // 사이즈와 색상 그룹화
        Set<ProductSizeAndColorQuantityDto> sizeAndColorQuantities = product.getSizeAndColorQuantities().stream()
                .collect(Collectors.groupingBy(
                        quantity -> quantity.getSizeType().name(), // 그룹 기준: 사이즈 타입
                        Collectors.toSet()
                ))
                .entrySet()
                .stream()
                .map(entry -> ProductSizeAndColorQuantityDto.builder()
                        .id(null) // 그룹화된 데이터는 단일 ID가 없으므로 null
                        .sizeType(entry.getKey())
                        .clothingSizes(entry.getValue().stream()
                                .filter(quantity -> quantity.getClothingSize() != null)
                                .map(quantity -> quantity.getClothingSize().name())
                                .collect(Collectors.toSet()))
                        .shoeSizes(entry.getValue().stream()
                                .filter(quantity -> quantity.getShoeSize() != null)
                                .map(quantity -> quantity.getShoeSize().name())
                                .collect(Collectors.toSet()))
                        .colors(entry.getValue().stream()
                                .map(quantity -> quantity.getColor().name())
                                .collect(Collectors.toSet()))
                        .quantity(entry.getValue().stream()
                                .mapToInt(ProductSizeAndColorQuantity::getQuantity)
                                .sum()) // 수량 합계
                        .build())
                .collect(Collectors.toSet());

        // DTO 생성 및 반환
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .sku(product.getSku())
                .mainCategory(new MainCategoryDto(product.getMainCategory()))
                .subCategory(new SubCategoryDto(product.getSubCategory()))
                .initialPrice(product.getInitialPrice())
                .description(product.getDescription())
                .images(images) // 이미지를 DTO에 추가
                .sizeAndColorQuantities(sizeAndColorQuantities)
                .build();
    }

    /**
     * 필터링된 상품 조회
     *
     * @param queryDslRequestDto 필터링 조건을 담은 DTO
     * @param pageable           페이징 정보
     * @return 필터링 및 페이징된 상품 리스트
     */
    public Page<ProductQueryDslResponseDto> searchFilteredProducts(ProductQueryDslRequestDto queryDslRequestDto, Pageable pageable) {
        return productQueryRepository.findProductsByFilter(
                queryDslRequestDto.getMainCategoryId(),
                queryDslRequestDto.getSubCategoryId(),
                queryDslRequestDto.getColor(),
                queryDslRequestDto.getSize(),
                queryDslRequestDto.getBrand(),
                queryDslRequestDto.getSortBy(),
                pageable
        );
    }
}
