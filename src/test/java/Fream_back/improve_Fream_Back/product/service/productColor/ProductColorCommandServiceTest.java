package Fream_back.improve_Fream_Back.product.service.productColor;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.ProductColorCreateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.ProductColorUpdateRequestDto;
import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.repository.ProductColorRepository;
import Fream_back.improve_Fream_Back.product.repository.ProductRepository;
import Fream_back.improve_Fream_Back.product.service.productDetailImage.ProductDetailImageCommandService;
import Fream_back.improve_Fream_Back.product.service.productImage.ProductImageCommandService;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestProductConfig.class)
@Transactional
class ProductColorCommandServiceTest {

    @Autowired
    private ProductColorCommandService productColorCommandService;

    @Autowired
    private TestProductConfig.TestData testData;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private ProductImageCommandService productImageCommandService;

    @Autowired
    private ProductDetailImageCommandService productDetailImageCommandService;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("유효한 색상과 이미지를 사용해 ProductColor 생성 테스트")
    void createProductColorWithValidData() {
        // Given
        Product product = testData.getProducts().get(0); // TestProductConfig에서 생성된 첫 번째 상품 사용
        ProductColorCreateRequestDto requestDto = ProductColorCreateRequestDto.builder()
                .colorName("BLACK")
                .content("Test content for BLACK color")
                .sizes(List.of("270", "280", "290"))
                .build();

        MultipartFile thumbnail = createMockMultipartFile("thumbnail.jpg", "image/jpeg");
        List<MultipartFile> productImages = List.of(
                createMockMultipartFile("product1.jpg", "image/jpeg"),
                createMockMultipartFile("product2.jpg", "image/jpeg")
        );
        List<MultipartFile> detailImages = List.of(
                createMockMultipartFile("detail1.jpg", "image/jpeg"),
                createMockMultipartFile("detail2.jpg", "image/jpeg")
        );

        // When
        productColorCommandService.createProductColor(requestDto, thumbnail, productImages, detailImages, product.getId());

        // Then
        List<ProductColor> colors = productColorRepository.findByProductId(product.getId());
        assertThat(colors).hasSize(4); // TestProductConfig에서 3개 색상 + 새로 생성한 색상

        ProductColor createdColor = colors.stream()
                .filter(color -> color.getColorName().equals("BLACK"))
                .findFirst()
                .orElseThrow();
        assertThat(createdColor.getProductImages()).hasSize(2);
        assertThat(createdColor.getProductDetailImages()).hasSize(2);
        System.out.println("createdColor.getThumbnailImage() = " + createdColor.getThumbnailImage());
        assertThat(createdColor.getThumbnailImage()).isNotNull();
    }

    @Test
    @DisplayName("유효하지 않은 색상으로 ProductColor 생성 시 예외 발생")
    void createProductColorWithInvalidColor() {
        // Given
        Product product = testData.getProducts().get(0);
        ProductColorCreateRequestDto requestDto = ProductColorCreateRequestDto.builder()
                .colorName("INVALID_COLOR")
                .content("Invalid color content")
                .sizes(List.of("S", "M", "L"))
                .build();

        // When & Then
        assertThatThrownBy(() -> productColorCommandService.createProductColor(
                requestDto, null, null, null, product.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 색상입니다");
    }

    @Test
    @DisplayName("ProductColor 업데이트 테스트")
    void updateProductColor() {
        // Given
        Product product = testData.getProducts().get(0);

        // ProductColor 생성
        ProductColorCreateRequestDto createRequestDto = ProductColorCreateRequestDto.builder()
                .colorName("BLACK")
                .content("Test content for BLACK color")
                .sizes(List.of("270", "280", "290"))
                .build();

        MultipartFile thumbnail = createMockMultipartFile("thumbnail.jpg", "image/jpeg");
        List<MultipartFile> productImages = List.of(
                createMockMultipartFile("product1.jpg", "image/jpeg"),
                createMockMultipartFile("product2.jpg", "image/jpeg")
        );
        List<MultipartFile> detailImages = List.of(
                createMockMultipartFile("detail1.jpg", "image/jpeg"),
                createMockMultipartFile("detail2.jpg", "image/jpeg")
        );

        productColorCommandService.createProductColor(createRequestDto, thumbnail, productImages, detailImages, product.getId());

        // Flush 영속성 컨텍스트 강제 반영
        entityManager.flush();
        entityManager.clear();

        // ProductColor 조회
        List<ProductColor> colors = productColorRepository.findByProductId(product.getId());
        ProductColor productColor = colors.stream()
                .filter(color -> color.getColorName().equals("BLACK"))
                .findFirst()
                .orElseThrow();

        // 데이터 검증
        assertThat(productColor.getProductImages()).isNotEmpty();
        assertThat(productColor.getProductDetailImages()).isNotEmpty();

        // 업데이트 요청
        ProductColorUpdateRequestDto updateRequestDto = ProductColorUpdateRequestDto.builder()
                .colorName("BLUE")
                .content("Updated content for BLUE color")
                .sizes(List.of("280", "290"))
                .existingImages(List.of(productColor.getProductImages().get(0).getImageUrl())) // 기존 이미지 중 하나 유지
                .existingDetailImages(List.of(productColor.getProductDetailImages().get(0).getImageUrl())) // 기존 상세 이미지 중 하나 유지
                .build();

        MultipartFile newThumbnail = createMockMultipartFile("new_thumbnail.jpg", "image/jpeg");
        List<MultipartFile> newImages = List.of(
                createMockMultipartFile("new_product1.jpg", "image/jpeg"),
                createMockMultipartFile("new_product2.jpg", "image/jpeg")
        );
        List<MultipartFile> newDetailImages = List.of(
                createMockMultipartFile("new_detail1.jpg", "image/jpeg")
        );

        // When
        productColorCommandService.updateProductColor(productColor.getId(), updateRequestDto, newThumbnail, newImages, newDetailImages);

        // Then
        ProductColor updatedColor = productColorRepository.findById(productColor.getId()).orElseThrow();
        assertThat(updatedColor.getColorName()).isEqualTo("BLUE");
        assertThat(updatedColor.getContent()).isEqualTo("Updated content for BLUE color");
        assertThat(updatedColor.getThumbnailImage()).isNotNull();
        assertThat(updatedColor.getProductImages()).hasSize(3); // 기존 유지 1개 + 새로 추가 2개
        assertThat(updatedColor.getProductDetailImages()).hasSize(2); // 기존 유지 1개 + 새로 추가 1개
        assertThat(updatedColor.getSizes().stream().map(ProductSize::getSize))
                .containsExactlyInAnyOrder("280", "290");
    }

    @Test
    @DisplayName("ProductColor 삭제 테스트")
    void deleteProductColor() {
        // Given
        Product product = testData.getProducts().get(0);
        ProductColor productColor = product.getColors().get(0);

        // Mock 이미지 파일 추가
        MultipartFile thumbnail = createMockMultipartFile("thumbnail.jpg", "image/jpeg");
        MultipartFile productImage = createMockMultipartFile("product1.jpg", "image/jpeg");
        MultipartFile detailImage = createMockMultipartFile("detail1.jpg", "image/jpeg");

        String thumbnailUrl = fileUtils.saveFile("product/" + product.getId() + "/", "thumbnail_", thumbnail);
        String productImageUrl = fileUtils.saveFile("product/" + product.getId() + "/", "product_", productImage);
        String detailImageUrl = fileUtils.saveFile("product/" + product.getId() + "/", "detail_", detailImage);

        ProductImage thumbnailEntity = productImageCommandService.createProductImage(thumbnailUrl, productColor);
        ProductImage productImageEntity = productImageCommandService.createProductImage(productImageUrl, productColor);
        ProductDetailImage detailImageEntity = ProductDetailImage.builder().imageUrl(detailImageUrl).productColor(productColor).build();
        productColor.addThumbnailImage(thumbnailEntity);
        productColor.addProductImage(productImageEntity);
        productColor.addProductDetailImage(detailImageEntity);

        productColorRepository.save(productColor);

        // When
        productColorCommandService.deleteProductColor(productColor.getId());

        // Then
        assertThat(productColorRepository.findById(productColor.getId())).isEmpty();
        assertThat(fileUtils.isFileExist(thumbnailUrl)).isFalse(); // 파일 삭제 확인
        assertThat(fileUtils.isFileExist(productImageUrl)).isFalse();
        assertThat(fileUtils.isFileExist(detailImageUrl)).isFalse();
    }


    private MultipartFile createMockMultipartFile(String name, String contentType) {
        return new MockMultipartFile(
                name,
                name,
                contentType,
                ("Dummy content for " + name).getBytes()
        );
    }
}
