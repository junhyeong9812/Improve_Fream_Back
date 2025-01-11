package Fream_back.improve_Fream_Back.product.service.productColor;

import Fream_back.improve_Fream_Back.product.dto.ProductColorCreateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.ProductColorUpdateRequestDto;
import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.ColorType;
import Fream_back.improve_Fream_Back.product.repository.ProductColorRepository;
import Fream_back.improve_Fream_Back.product.service.interest.InterestCommandService;
import Fream_back.improve_Fream_Back.product.service.product.ProductEntityService;
import Fream_back.improve_Fream_Back.product.service.productDetailImage.ProductDetailImageCommandService;
import Fream_back.improve_Fream_Back.product.service.productDetailImage.ProductDetailImageQueryService;
import Fream_back.improve_Fream_Back.product.service.productImage.ProductImageCommandService;
import Fream_back.improve_Fream_Back.product.service.productImage.ProductImageQueryService;
import Fream_back.improve_Fream_Back.product.service.productSize.ProductSizeCommandService;
import Fream_back.improve_Fream_Back.product.service.productSize.ProductSizeQueryService;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import Fream_back.improve_Fream_Back.utils.NginxCachePurgeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductColorCommandService {

    private final ProductColorRepository productColorRepository;
    private final ProductSizeCommandService productSizeCommandService;
    private final FileUtils fileUtils;
    private final ProductEntityService productEntityService;
    private final ProductSizeQueryService productSizeQueryService;
    private final InterestCommandService interestCommandService;
    private final ProductImageCommandService productImageCommandService;
    private final ProductDetailImageCommandService productDetailImageCommandService;
    private final ProductImageQueryService productImageQueryService;
    private final ProductDetailImageQueryService productDetailImageQueryService;
    private final NginxCachePurgeUtil nginxCachePurgeUtil;
    private final JobLauncher jobLauncher;      // 배치 런처
    private final Job createSizesJob;           // 위에서 정의한 잡

    // 기본 파일 저장 경로
    private final String BASE_DIRECTORY = System.getProperty("user.dir") +  "/product/";

    public Long createProductColor(
            ProductColorCreateRequestDto requestDto,
            MultipartFile productImage,
            List<MultipartFile> productImages, // 일반 이미지 리스트
            List<MultipartFile> productDetailImages,
            Long productId) {
        // ColorType 유효성 검증
        validateColorType(requestDto.getColorName());

        // Product 엔티티 조회
        Product product = productEntityService.findById(productId);

        // ProductColor 생성
        ProductColor productColor = ProductColor.builder()
                .colorName(requestDto.getColorName())
                .content(requestDto.getContent())
                .product(product)
                .build();

        // Product/{productId}/ 디렉토리 경로 생성
        String productDirectory = BASE_DIRECTORY + product.getId() + "/";

        // ProductImage 저장 (썸네일)
        if (productImage != null) {
            String thumbnailFilename = fileUtils.saveFile(productDirectory, "thumbnail_", productImage);
            ProductImage thumbnail = ProductImage.builder()
                    .imageUrl(productDirectory + thumbnailFilename)
                    .build();
            productColor.addThumbnailImage(thumbnail);
        }

        // ProductImages 저장 (일반 이미지)
        if (productImages != null && !productImages.isEmpty()) {
            productImages.forEach(file -> {
                String imageFilename = fileUtils.saveFile(productDirectory, "ProductImage_", file);
                ProductImage productImageEntity = ProductImage.builder()
                        .imageUrl(productDirectory + imageFilename)
                        .build();
                productColor.addProductImage(productImageEntity);
            });
        }

        // ProductDetailImage 저장 (상세페이지 이미지)
        if (productDetailImages != null && !productDetailImages.isEmpty()) {
            productDetailImages.forEach(file -> {
                String detailFilename = fileUtils.saveFile(productDirectory, "ProductDetailImage_", file);
                ProductDetailImage detailImage = ProductDetailImage.builder()
                        .imageUrl(productDirectory + detailFilename)
                        .build();
                productColor.addProductDetailImage(detailImage);
            });
        }

        // ProductColor 생성
        ProductColor savedColor = productColorRepository.save(productColor);

        // 사이즈 생성
        productSizeCommandService.createProductSizes(savedColor,  product.getCategory().getId(),requestDto.getSizes(),product.getReleasePrice());
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addLong("productColorId", savedColor.getId())
//                .addLong("categoryId", product.getCategory().getId())
//                .addString("requestedSizes", String.join(",", requestDto.getSizes()))
//                .addLong("releasePrice",(long)product.getReleasePrice())
//                .addLong("timestamp", System.currentTimeMillis()) // 실행마다 달라야 함
//                .toJobParameters();
//
//        // 3) 배치 Job 실행
//        try {
//            jobLauncher.run(createSizesJob, jobParameters);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 예외 처리
//        }


        return savedColor.getId();
    }

    private void validateColorType(String colorName) {
        boolean isValid = Arrays.stream(ColorType.values())
                .anyMatch(colorType ->
                        colorType.name().equalsIgnoreCase(colorName.trim()) ||
                                colorType.getDisplayName().equals(colorName.trim())
                ); // 두 가지 방식 비교
        if (!isValid) {
            throw new IllegalArgumentException("유효하지 않은 색상입니다: " + colorName);
        }
    }

    @Transactional
    public void updateProductColor(
            Long productColorId,
            ProductColorUpdateRequestDto requestDto,
            MultipartFile thumbnailImage,
            List<MultipartFile> newImages,
            List<MultipartFile> newDetailImages) {

        // ProductColor 엔티티 조회
        ProductColor productColor = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 색상을 찾을 수 없습니다."));

        // Product/{productId}/ 디렉토리 경로 생성
        Product product = productColor.getProduct();
        String productDirectory = BASE_DIRECTORY + product.getId() + "/";



        // 썸네일 이미지 처리
        if (thumbnailImage != null) {
            if (productColor.getThumbnailImage() != null) {
                fileUtils.deleteFile(productDirectory, productColor.getThumbnailImage().getImageUrl());
            }
            String thumbnailFilename = fileUtils.saveFile(productDirectory, "thumbnail_", thumbnailImage);
            ProductImage newThumbnail = ProductImage.builder()
                    .imageUrl(thumbnailFilename)
                    .build();
            productColor.addThumbnailImage(newThumbnail);
        }

        // 기존 일반 이미지 처리
        if (requestDto.getExistingImages() != null) {
            productColor.getProductImages().removeIf(image -> {
                if (!requestDto.getExistingImages().contains(image.getImageUrl())) {
                    fileUtils.deleteFile(productDirectory, image.getImageUrl());
                    return true;
                }
                return false;
            });
        }

        // 새 일반 이미지 추가
        if (newImages != null && !newImages.isEmpty()) {
            newImages.forEach(file -> {
                String imageFilename = fileUtils.saveFile(productDirectory, "ProductImage_", file);
                ProductImage newImage = ProductImage.builder()
                        .imageUrl(imageFilename)
                        .build();
                productColor.addProductImage(newImage);
            });
        }

        // 기존 상세 이미지 처리
        if (requestDto.getExistingDetailImages() != null) {
            productColor.getProductDetailImages().removeIf(detailImage -> {
                if (!requestDto.getExistingDetailImages().contains(detailImage.getImageUrl())) {
                    fileUtils.deleteFile(productDirectory, detailImage.getImageUrl());
                    return true;
                }
                return false;
            });
        }

        // 새 상세 이미지 추가
        if (newDetailImages != null && !newDetailImages.isEmpty()) {
            newDetailImages.forEach(file -> {
                String detailFilename = fileUtils.saveFile(productDirectory, "ProductDetailImage_", file);
                ProductDetailImage newDetailImage = ProductDetailImage.builder()
                        .imageUrl(detailFilename)
                        .build();
                productColor.addProductDetailImage(newDetailImage);
            });
        }


        // 사이즈 업데이트 처리
        if (requestDto.getSizes() != null) {
            List<String> updatedSizes = requestDto.getSizes();

            // 기존 사이즈 조회
            List<String> existingSizes = productColor.getSizes()
                    .stream()
                    .map(ProductSize::getSize)
                    .toList();

            // 삭제할 사이즈 처리
            existingSizes.stream()
                    .filter(size -> !updatedSizes.contains(size))
                    .forEach(size -> {
                        ProductSize sizeEntity = productColor.getSizes()
                                .stream()
                                .filter(existingSize -> existingSize.getSize().equals(size))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("해당 사이즈를 찾을 수 없습니다: " + size));
                        // 엔티티 삭제
                        productSizeCommandService.deleteProductSize(sizeEntity.getId());

                        // 컬렉션에서 제거
                        productColor.getSizes().remove(sizeEntity);
                    });

            // 추가할 사이즈 처리
            List<String> newSizes = updatedSizes.stream()
                    .filter(size -> !existingSizes.contains(size))
                    .toList();

            if (!newSizes.isEmpty()) {
                productSizeCommandService.createProductSizes(
                        productColor,
                        productColor.getProduct().getCategory().getId(),
                        newSizes,
                        productColor.getProduct().getReleasePrice()
                );
            }
        }

        // 기본 정보 업데이트
        productColor.update(requestDto.getColorName(), requestDto.getContent());

        // 저장
        productColorRepository.save(productColor);
    }

    @Transactional
    public void deleteProductColor(Long productColorId) {
        ProductColor productColor = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 색상을 찾을 수 없습니다."));

        // Product/{productId}/ 디렉토리 경로 생성
        Product product = productColor.getProduct();
        String productDirectory = BASE_DIRECTORY + product.getId() + "/";

        // 관심 상품 삭제
        interestCommandService.deleteAllInterestsByProductColor(productColor);

        // 사이즈 삭제
        productSizeCommandService.deleteAllSizesByProductColor(productColor);

        // 이미지 삭제
        if (productColor.getThumbnailImage() != null) {
            fileUtils.deleteFile(productDirectory, productColor.getThumbnailImage().getImageUrl()); // 실제 파일 삭제
            productImageCommandService.deleteProductImage(productColor.getThumbnailImage().getId()); // 엔티티 삭제
        }

        // 일반 이미지 삭제
        if (productImageQueryService.existsByProductColorId(productColorId)) {
            productImageQueryService.findAllByProductColorId(productColorId).forEach(image -> {
                if (fileUtils.existsFile(productDirectory, image.getImageUrl())) {
                    fileUtils.deleteFile(productDirectory, image.getImageUrl());
                }
                productImageCommandService.deleteProductImage(image.getId());
            });
        }

        // 상세 이미지 삭제
        if (productDetailImageQueryService.existsByProductColorId(productColorId)) {
            productDetailImageQueryService.findAllByProductColorId(productColorId).forEach(detailImage -> {
                if (fileUtils.existsFile(BASE_DIRECTORY, detailImage.getImageUrl())) {
                    fileUtils.deleteFile(BASE_DIRECTORY, detailImage.getImageUrl());
                }
                productDetailImageCommandService.deleteProductDetailImage(detailImage.getId());
            });
        }

        // 모든 이미지를 컬렉션에서 제거
        productColor.getProductImages().clear();
        productColor.getProductDetailImages().clear();

        // ProductColor 삭제
        productColorRepository.delete(productColor);
    }


}