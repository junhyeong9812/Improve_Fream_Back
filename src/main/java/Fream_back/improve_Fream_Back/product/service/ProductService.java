package Fream_back.improve_Fream_Back.product.service;

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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

        productRepository.save(product);

        // 상품 이미지 추가
        if (productDto.getImages() != null) {
            productDto.getImages().forEach(imageDto -> {
                try {
                    String imageFileName = saveImageToFileSystem(imageDto.getImageUrl(), product.getId());
                    ProductImage productImage = ProductImage.builder()
                            .imageUrl(imageFileName)
                            .imageType(imageDto.getImageType())
                            .isMainThumbnail(imageDto.isMainThumbnail())
                            .build();
                    productImage.assignProduct(product);
                    productImageRepository.save(productImage); // 영속성 컨텍스트에 저장
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save image", e);
                }
            });
        }

        // 사이즈 및 색상별 수량 추가 (모든 조합 생성)
        if (productDto.getSizeAndColorQuantities() != null) {
            productDto.getSizeAndColorQuantities().forEach(sizeAndColorDto -> {
                SizeType sizeType = SizeType.valueOf(sizeAndColorDto.getSizeType());

                if (sizeType == SizeType.CLOTHING) {
                    sizeAndColorDto.getColors().forEach(color -> {
                        Color colorEnum = Color.valueOf(color);
                        sizeAndColorDto.getClothingSizes().forEach(clothingSize -> {
                            ProductSizeAndColorQuantity sizeAndColorQuantity = ProductSizeAndColorQuantity.builder()
                                    .sizeType(sizeType)
                                    .clothingSize(ClothingSizeType.valueOf(clothingSize))
                                    .color(colorEnum)
                                    .quantity(sizeAndColorDto.getQuantity())
                                    .build();
                            sizeAndColorQuantity.assignProduct(product);
                            // product.addSizeAndColorQuantity(sizeAndColorQuantity); // Removed as size and color quantities are managed separately from Product
                            productSizeAndColorQuantityRepository.save(sizeAndColorQuantity); // 영속성 컨텍스트에 저장
                        });
                    });
                } else if (sizeType == SizeType.SHOES) {
                    sizeAndColorDto.getColors().forEach(color -> {
                        Color colorEnum = Color.valueOf(color);
                        sizeAndColorDto.getShoeSizes().forEach(shoeSize -> {
                            ProductSizeAndColorQuantity sizeAndColorQuantity = ProductSizeAndColorQuantity.builder()
                                    .sizeType(sizeType)
                                    .shoeSize(ShoeSizeType.valueOf(shoeSize))
                                    .color(colorEnum)
                                    .quantity(sizeAndColorDto.getQuantity())
                                    .build();
                            sizeAndColorQuantity.assignProduct(product);
                            // product.addSizeAndColorQuantity(sizeAndColorQuantity); // Removed as size and color quantities are managed separately from Product
                            productSizeAndColorQuantityRepository.save(sizeAndColorQuantity); // 영속성 컨텍스트에 저장
                        });
                    });
                }
            });
        }

        return product;
    }

    //파일 저장
    private String saveImageToFileSystem(String imageUrl, Long productId) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniqueFileName = "product_" + productId + "_" + timestamp + "_" + UUID.randomUUID() + ".jpg";
        Path imagePath = Paths.get("images/" + uniqueFileName);

        // 임시로 URL을 파일로 저장하는 부분 (실제로는 파일 업로드 기능 필요)
        Files.copy(Paths.get(imageUrl), imagePath);

        return imagePath.toString();
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

//        // 이미지 수정 (더티 체크 활용)
//        // product.clearProductImages(); // Removed as images are managed separately from Product
//        if (productDto.getImages() != null) {
//            productDto.getImages().forEach(imageDto -> {
//                ProductImage productImage = ProductImage.builder()
//                        .imageUrl(imageDto.getImageUrl())
//                        .imageType(imageDto.getImageType())
//                        .isMainThumbnail(imageDto.isMainThumbnail())
//                        .build();
//                productImage.assignProduct(product);
//                // product.addProductImage(productImage); // Removed as images are not associated directly with Product in the current design
//            });
//        }
        // 기존 이미지와 비교하여 업데이트
        if (productDto.getImages() != null) {
            Set<String> updatedImageUrls = productDto.getImages().stream()
                    .map(ProductImageDto::getImageUrl)
                    .collect(Collectors.toSet());

            // 기존 이미지 중에서 업데이트된 리스트에 없는 이미지를 삭제
            List<ProductImage> existingImages = productImageRepository.findAllByProductId(productId);
            existingImages.forEach(existingImage -> {
                if (!updatedImageUrls.contains(existingImage.getImageUrl())) {
                    deleteImageFile(existingImage.getImageUrl());
                    productImageRepository.delete(existingImage);
                }
            });

            // 새로운 이미지를 추가
            productDto.getImages().forEach(imageDto -> {
                if (existingImages.stream().noneMatch(existingImage -> existingImage.getImageUrl().equals(imageDto.getImageUrl()))) {
                    try {
                        String newImageFileName = saveImageToFileSystem(imageDto.getImageUrl(), product.getId());
                        ProductImage newProductImage = ProductImage.builder()
                                .imageUrl(newImageFileName)
                                .imageType(imageDto.getImageType())
                                .isMainThumbnail(imageDto.isMainThumbnail())
                                .build();
                        newProductImage.assignProduct(product);
                        productImageRepository.save(newProductImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to save new image", e);
                    }
                }
            });
        }

        // 사이즈 및 색상별 수량 수정 (더티 체크 활용)
        // product.clearSizeAndColorQuantities(); // Removed as size and color quantities are managed separately from Product
        if (productDto.getSizeAndColorQuantities() != null) {
            productDto.getSizeAndColorQuantities().forEach(sizeAndColorDto -> {
                SizeType sizeType = SizeType.valueOf(sizeAndColorDto.getSizeType());

                if (sizeType == SizeType.CLOTHING) {
                    sizeAndColorDto.getColors().forEach(color -> {
                        Color colorEnum = Color.valueOf(color);
                        sizeAndColorDto.getClothingSizes().forEach(clothingSize -> {
                            ProductSizeAndColorQuantity sizeAndColorQuantity = productSizeAndColorQuantityRepository.findByProductIdAndClothingSizeAndColor(productId, ClothingSizeType.valueOf(clothingSize), colorEnum)
                                    .orElseGet(() -> ProductSizeAndColorQuantity.builder()
                                            .sizeType(sizeType)
                                            .clothingSize(ClothingSizeType.valueOf(clothingSize))
                                            .color(colorEnum)
                                            .build());
                            sizeAndColorQuantity.updateQuantity(sizeAndColorDto.getQuantity());
                            sizeAndColorQuantity.assignProduct(product);
                            // product.addSizeAndColorQuantity(sizeAndColorQuantity); // Removed as size and color quantities are managed separately from Product
                        });
                    });
                } else if (sizeType == SizeType.SHOES) {
                    sizeAndColorDto.getColors().forEach(color -> {
                        Color colorEnum = Color.valueOf(color);
                        sizeAndColorDto.getShoeSizes().forEach(shoeSize -> {
                            ProductSizeAndColorQuantity sizeAndColorQuantity = productSizeAndColorQuantityRepository.findByProductIdAndShoeSizeAndColor(productId, ShoeSizeType.valueOf(shoeSize), colorEnum)
                                    .orElseGet(() -> ProductSizeAndColorQuantity.builder()
                                            .sizeType(sizeType)
                                            .shoeSize(ShoeSizeType.valueOf(shoeSize))
                                            .color(colorEnum)
                                            .build());
                            sizeAndColorQuantity.updateQuantity(sizeAndColorDto.getQuantity());
                            sizeAndColorQuantity.assignProduct(product);
                            // product.addSizeAndColorQuantity(sizeAndColorQuantity); // Removed as size and color quantities are managed separately from Product
                        });
                    });
                }
            });
        }

        return product;
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
    // 파일 시스템에서 이미지 파일을 삭제하는 메서드
    private void deleteImageFile(String imageUrl) {
        Path imagePath = Paths.get("images/" + imageUrl);
        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image file", e);
        }
    }
}

