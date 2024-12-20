package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class) // QueryDSL 설정 필요 시 추가
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Test
    @DisplayName("상품 저장 및 조회 테스트")
    void saveAndFindProduct() {
        // Given
        Brand brand = brandRepository.save(Brand.builder().name("Nike").build());
        Category category = categoryRepository.save(Category.builder().name("Shoes").build());
        Collection collection = collectionRepository.save(Collection.builder().name("Spring Collection").build());

        Product product = Product.builder()
                .name("Air Max")
                .englishName("Air Max 2023")
                .releasePrice(150)
                .modelNumber("AM2023")
                .releaseDate("2023-01-01")
                .gender(GenderType.UNISEX)
                .brand(brand)
                .category(category)
                .collection(collection)
                .build();

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        Optional<Product> result = productRepository.findById(savedProduct.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Air Max");
        assertThat(result.get().getBrand().getName()).isEqualTo("Nike");
        assertThat(result.get().getCategory().getName()).isEqualTo("Shoes");
        assertThat(result.get().getCollection().getName()).isEqualTo("Spring Collection");
    }

    @Test
    @DisplayName("상품 업데이트 테스트")
    void updateProduct() {
        // Given
        Brand brand = brandRepository.save(Brand.builder().name("Adidas").build());
        Category category = categoryRepository.save(Category.builder().name("Clothing").build());

        Product product = Product.builder()
                .name("Ultraboost")
                .englishName("Ultraboost 2023")
                .releasePrice(180)
                .modelNumber("UB2023")
                .releaseDate("2023-02-01")
                .gender(GenderType.MALE)
                .brand(brand)
                .category(category)
                .build();
        Product savedProduct = productRepository.save(product);

        // When
        savedProduct.update("Ultraboost Updated", "Ultraboost 2023 V2", 200, "UB2023V2", "2023-03-01");
        productRepository.save(savedProduct);

        // Then
        Optional<Product> result = productRepository.findById(savedProduct.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Ultraboost Updated");
        assertThat(result.get().getReleasePrice()).isEqualTo(200);
        assertThat(result.get().getModelNumber()).isEqualTo("UB2023V2");
        assertThat(result.get().getReleaseDate()).isEqualTo("2023-03-01");
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    void deleteProduct() {
        // Given
        Product product = Product.builder()
                .name("Air Force 1")
                .englishName("AF1")
                .releasePrice(100)
                .modelNumber("AF1001")
                .releaseDate("2023-04-01")
                .gender(GenderType.FEMALE)
                .build();
        Product savedProduct = productRepository.save(product);

        // When
        productRepository.delete(savedProduct);

        // Then
        Optional<Product> result = productRepository.findById(savedProduct.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("상품과 연관된 색상 추가 테스트")
    void addProductColor() {
        // Given
        Product product = Product.builder()
                .name("Jordan")
                .englishName("Air Jordan")
                .releasePrice(200)
                .modelNumber("AJ2023")
                .releaseDate("2023-05-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductColor color = ProductColor.builder()
                .colorName("Red")
                .product(savedProduct)
                .build();

        // When
        ProductColor savedColor = productColorRepository.save(color);

        // Then
        Optional<ProductColor> result = productColorRepository.findById(savedColor.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getColorName()).isEqualTo("Red");
        assertThat(result.get().getProduct().getId()).isEqualTo(savedProduct.getId());
    }

    @Test
    @DisplayName("상품 색상 삭제 테스트")
    void deleteProductColor() {
        // Given
        Product product = Product.builder()
                .name("Jordan")
                .englishName("Air Jordan")
                .releasePrice(200)
                .modelNumber("AJ2023")
                .releaseDate("2023-05-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductColor color = ProductColor.builder()
                .colorName("Blue")
                .product(savedProduct)
                .build();
        ProductColor savedColor = productColorRepository.save(color);

        // When
        productColorRepository.delete(savedColor);

        // Then
        Optional<ProductColor> result = productColorRepository.findById(savedColor.getId());
        assertThat(result).isEmpty();
    }
}
