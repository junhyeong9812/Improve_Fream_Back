package Fream_back.improve_Fream_Back.product.service.productColor;

import Fream_back.improve_Fream_Back.product.dto.ProductColorCreateRequestDto;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.entity.ProductDetailImage;
import Fream_back.improve_Fream_Back.product.entity.ProductImage;
import Fream_back.improve_Fream_Back.product.repository.ProductColorRepository;
import Fream_back.improve_Fream_Back.product.service.product.ProductEntityService;
import Fream_back.improve_Fream_Back.product.service.productSize.ProductSizeCommandService;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductColorCommandService {

    private final ProductColorRepository productColorRepository;
    private final ProductSizeCommandService productSizeCommandService;
    private final FileUtils fileUtils;
    private final ProductEntityService productEntityService;

    // 기본 파일 저장 경로
    private final String BASE_DIRECTORY = "product/";



    public void createProductColor(
            ProductColorCreateRequestDto requestDto,
            MultipartFile productImage,
            List<MultipartFile> productDetailImages,
            Long productId) {

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
            String thumbnailFilename = fileUtils.saveFile(productDirectory, "ProductImage_", productImage);
            ProductImage thumbnail = ProductImage.builder()
                    .imageUrl(productDirectory + thumbnailFilename)
                    .build();
            productColor.addProductImage(thumbnail);
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
        productSizeCommandService.createProductSizes(savedColor,  product.getCategory(),requestDto.getSizes(),product.getReleasePrice());
    }
}