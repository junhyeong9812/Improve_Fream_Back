package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.dto.create.ProductCreateRequestDto;
import Fream_back.improve_Fream_Back.product.entity.size.ProductSizeAndColorQuantity;
import Fream_back.improve_Fream_Back.product.service.fileStorageUtil.FileStorageUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductSizeAndColorQuantityRepository productSizeAndColorQuantityRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public FileStorageUtil fileStorageUtil() {
            return new FileStorageUtil();
        }
    }

    @Test
    @DisplayName("프로덕트 생성 테스트")
    public void testCreateProduct() throws IOException {
        // given
        String tempFilePath1 = fileStorageUtil.saveTemporaryFile(new MockMultipartFile(
                "file", "image1.jpg", "image/jpeg", "dummy content".getBytes()));
        String tempFilePath2 = fileStorageUtil.saveTemporaryFile(new MockMultipartFile(
                "file", "image2.jpg", "image/jpeg", "dummy content".getBytes()));

        ProductCreateRequestDto createRequestDto = ProductCreateRequestDto.builder()
                .name("티셔츠")
                .brand("브랜드A")
                .mainCategoryId(1L)
                .subCategoryId(2L)
                .initialPrice(new BigDecimal("29900"))
                .description("편안한 티셔츠")
                .images(List.of(ProductImageDto.builder()
                                .imageName("image1.jpg")
                                .temp_Url(tempFilePath1)
                                .imageType("thumbnail")
                                .isMainThumbnail(true)
                                .build(),
                        ProductImageDto.builder()
                                .imageName("image2.jpg")
                                .temp_Url(tempFilePath2)
                                .imageType("detail")
                                .isMainThumbnail(false)
                                .build()))
                .sizeAndColorQuantities(Set.of(
                        ProductSizeAndColorQuantityDto.builder()
                                .sizeType("CLOTHING")
                                .clothingSizes(Set.of("M", "L"))  // Set으로 감싸서 넣기
                                .shoeSizes(Set.of())  // 신발 사이즈가 없으므로 빈 Set으로 처리
                                .colors(Set.of("BLACK", "WHITE"))  // 색상도 Set으로 감싸기
                                .quantity(10)
                                .build()
                ))
                .build();

        // DTO를 엔티티로 변환하고, Product를 먼저 저장해서 영속 상태로 만듭니다.
        Product tempProduct = productRepository.save(createRequestDtoToEntity(createRequestDto));

//        // 연관 관계 설정 - ProductSizeAndColorQuantity 추가
//        createRequestDto.getSizeAndColorQuantities().forEach(sizeAndColorDto -> {
//            ProductSizeAndColorQuantity sizeAndColorQuantity = ProductSizeAndColorQuantity.builder()
//                    .sizeType(SizeType.valueOf(sizeAndColorDto.getSizeType()))
//                    .clothingSize(sizeAndColorDto.getClothingSize() != null ? ClothingSizeType.valueOf(sizeAndColorDto.getClothingSize()) : null)
//                    .shoeSize(sizeAndColorDto.getShoeSize() != null ? ShoeSizeType.valueOf(sizeAndColorDto.getShoeSize()) : null)
//                    .color(Color.valueOf(sizeAndColorDto.getColor()))
//                    .quantity(sizeAndColorDto.getQuantity())
//                    .product(tempProduct) // 영속 상태인 product 설정
//                    .build();
//            tempProduct.addSizeAndColorQuantity(sizeAndColorQuantity);
//        });
        // 연관 관계 설정 - ProductSizeAndColorQuantity 추가
        createRequestDto.getSizeAndColorQuantities().forEach(sizeAndColorDto -> {
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
                                .product(tempProduct) // 영속 상태인 product 설정
                                .build();
                        tempProduct.addSizeAndColorQuantity(sizeAndColorQuantity);
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
                                .product(tempProduct) // 영속 상태인 product 설정
                                .build();
                        tempProduct.addSizeAndColorQuantity(sizeAndColorQuantity);
                    });
                });
            }
        });

        // 최종적으로 Product를 다시 저장하여 연관 엔티티들도 저장
        Product product = productRepository.save(tempProduct);

        // 이미지 저장
        createRequestDto.getImages().forEach(imageDto -> {
            ProductImage productImage = ProductImage.builder()
                    .imageUrl(imageDto.getImageUrl())
                    .imageType(imageDto.getImageType())
                    .isMainThumbnail(imageDto.isMainThumbnail())
                    .product(product)
                    .build();
            productImageRepository.save(productImage);
        });

        // then
        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo("티셔츠");
        assertThat(productSizeAndColorQuantityRepository.findAllByProductId(product.getId())).hasSize(4);
        assertThat(productImageRepository.findAllByProductId(product.getId())).hasSize(2);
    }

    @Test
    @DisplayName("프로덕트 삭제 테스트")
    public void testDeleteProduct() {
        // given
        Product product = productRepository.save(Product.builder().name("티셔츠").brand("브랜드A").sku("SKU1234").build());

        // when
        productRepository.delete(product);

        // then
        assertThat(productRepository.findById(product.getId())).isNotPresent();
    }

    @Test
    @DisplayName("프로덕트 조회 테스트")
    public void testFindProduct() {
        // given
        Product product = productRepository.save(Product.builder().name("티셔츠").brand("브랜드A").sku("SKU1234").build());

        // when
        Product foundProduct = productRepository.findByIdWithDetails(product.getId());

        // then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("티셔츠");
    }

    @Test
    @DisplayName("프로덕트 수정 테스트")
    public void testUpdateProduct() {
        // given
        Product product = productRepository.save(Product.builder().name("티셔츠").brand("브랜드A").sku("SKU1234").build());
        ProductUpdateRequestDto updateRequestDto = ProductUpdateRequestDto.builder()
                .id(product.getId())
                .name("수정된 티셔츠")
                .brand("브랜드B")
                .sku("SKU5678")
                .initialPrice(new BigDecimal("39900"))
                .description("수정된 설명")
                .build();

        // when
        Product updatedProduct = productRepository.save(updateRequestDtoToEntity(updateRequestDto, product));

        // then
        assertThat(updatedProduct.getName()).isEqualTo("수정된 티셔츠");
        assertThat(updatedProduct.getBrand()).isEqualTo("브랜드B");
    }

//    @Test
//    @DisplayName("쿼리DSL로 필터링된 프로덕트 조회 테스트")
//    public void testFindProductsByFilter() {
//        // given
//        Product product1 = productRepository.save(Product.builder().name("티셔츠").brand("브랜드A").sku("SKU1234").build());
//        Product product2 = productRepository.save(Product.builder().name("운동화").brand("브랜드B").sku("SKU5678").build());
//
//        ProductSizeAndColorQuantity sizeAndColor1 = ProductSizeAndColorQuantity.builder()
//                .product(product1)
//                .sizeType(SizeType.CLOTHING)
//                .color(Color.BLACK)
//                .clothingSize(ClothingSizeType.M)
//                .quantity(10)
//                .build();
//        productSizeAndColorQuantityRepository.save(sizeAndColor1);
//
//        // when
//        ProductQueryRepository productQueryRepository = new ProductQueryRepository(entityManager);
//        List<ProductQueryDslResponseDto> products = productQueryRepository.findProductsByFilter(null, null, "BLACK", null);
//
//        // then
//        assertThat(products).hasSize(1);
//        assertThat(products.get(0).getName()).isEqualTo("티셔츠");
//    }

    private Product createRequestDtoToEntity(ProductCreateRequestDto dto) {
        return Product.builder()
                .name(dto.getName())
                .brand(dto.getBrand())
                .initialPrice(dto.getInitialPrice())
                .description(dto.getDescription())
                .build();
    }

    private Product updateRequestDtoToEntity(ProductUpdateRequestDto dto, Product product) {
        return Product.builder()
                .id(product.getId())
                .name(dto.getName() != null ? dto.getName() : product.getName())
                .brand(dto.getBrand() != null ? dto.getBrand() : product.getBrand())
                .sku(dto.getSku() != null ? dto.getSku() : product.getSku())
                .mainCategory(product.getMainCategory())
                .subCategory(product.getSubCategory())
                .initialPrice(dto.getInitialPrice() != null ? dto.getInitialPrice() : product.getInitialPrice())
                .description(dto.getDescription() != null ? dto.getDescription() : product.getDescription())
                .sizeAndColorQuantities(product.getSizeAndColorQuantities())
                .userProducts(product.getUserProducts())
                .build();
    }
}