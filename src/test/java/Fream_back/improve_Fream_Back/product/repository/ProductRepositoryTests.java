package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.ProductImage;
import Fream_back.improve_Fream_Back.product.entity.enumType.ClothingSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.Color;
import Fream_back.improve_Fream_Back.product.entity.enumType.ShoeSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import Fream_back.improve_Fream_Back.product.entity.size.ProductSizeAndColorQuantity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

    @Test
    @DisplayName("프로덕트 생성 테스트")
    public void testCreateProduct() {
        // given
        ProductCreateRequestDto createRequestDto = ProductCreateRequestDto.builder()
                .name("티셔츠")
                .brand("브랜드A")
                .sku("SKU1234")
                .mainCategoryId(1L)
                .subCategoryId(2L)
                .initialPrice(new BigDecimal("29900"))
                .description("편안한 티셔츠")
                .images(List.of(new ProductImageDto(null, "image1.jpg", "thumbnail", true),
                        new ProductImageDto(null, "image2.jpg", "detail", false)))
                .sizeAndColorQuantities(Set.of(new ProductSizeAndColorQuantityDto(null, "CLOTHING", "M", null, "BLACK", 10)))
                .build();

        // DTO를 엔티티로 변환하고, Product를 먼저 저장해서 영속 상태로 만듭니다.
        Product tempProduct = productRepository.save(createRequestDtoToEntity(createRequestDto));

        // 연관 관계 설정 - ProductSizeAndColorQuantity 추가
        createRequestDto.getSizeAndColorQuantities().forEach(sizeAndColorDto -> {
            ProductSizeAndColorQuantity sizeAndColorQuantity = ProductSizeAndColorQuantity.builder()
                    .sizeType(SizeType.valueOf(sizeAndColorDto.getSizeType()))
                    .clothingSize(sizeAndColorDto.getClothingSize() != null ? ClothingSizeType.valueOf(sizeAndColorDto.getClothingSize()) : null)
                    .shoeSize(sizeAndColorDto.getShoeSize() != null ? ShoeSizeType.valueOf(sizeAndColorDto.getShoeSize()) : null)
                    .color(Color.valueOf(sizeAndColorDto.getColor()))
                    .quantity(sizeAndColorDto.getQuantity())
                    .product(tempProduct) // 영속 상태인 product 설정
                    .build();
            tempProduct.addSizeAndColorQuantity(sizeAndColorQuantity);
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
        assertThat(productSizeAndColorQuantityRepository.findAllByProductId(product.getId())).hasSize(1);
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

    @Test
    @DisplayName("쿼리DSL로 필터링된 프로덕트 조회 테스트")
    public void testFindProductsByFilter() {
        // given
        Product product1 = productRepository.save(Product.builder().name("티셔츠").brand("브랜드A").sku("SKU1234").build());
        Product product2 = productRepository.save(Product.builder().name("운동화").brand("브랜드B").sku("SKU5678").build());

        ProductSizeAndColorQuantity sizeAndColor1 = ProductSizeAndColorQuantity.builder()
                .product(product1)
                .sizeType(SizeType.CLOTHING)
                .color(Color.BLACK)
                .clothingSize(ClothingSizeType.M)
                .quantity(10)
                .build();
        productSizeAndColorQuantityRepository.save(sizeAndColor1);

        // when
        ProductQueryRepository productQueryRepository = new ProductQueryRepository(entityManager);
        List<ProductQueryDslResponseDto> products = productQueryRepository.findProductsByFilter(null, null, "BLACK", null);

        // then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("티셔츠");
    }

    private Product createRequestDtoToEntity(ProductCreateRequestDto dto) {
        return Product.builder()
                .name(dto.getName())
                .brand(dto.getBrand())
                .sku(dto.getSku())
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