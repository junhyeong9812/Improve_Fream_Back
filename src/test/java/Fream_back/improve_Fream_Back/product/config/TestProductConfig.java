package Fream_back.improve_Fream_Back.product.config;

import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.ColorType;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import Fream_back.improve_Fream_Back.product.repository.*;
import lombok.Getter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@TestConfiguration
public class TestProductConfig {

    private final Random random = new Random();

    @Bean
    public TestData setupTestData(
            BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            CollectionRepository collectionRepository,
            ProductRepository productRepository,
            ProductColorRepository productColorRepository,
            ProductSizeRepository productSizeRepository
    ) {
        // 브랜드 생성
        Brand nike = brandRepository.save(Brand.builder().name("Nike").build());
        Brand adidas = brandRepository.save(Brand.builder().name("Adidas").build());
        Brand reebok = brandRepository.save(Brand.builder().name("Reebok").build());
        List<Brand> brands = List.of(nike, adidas, reebok);

        // 카테고리 생성
        Category shoesCategory = categoryRepository.save(Category.builder().name("Shoes").build());
        Category sneakersCategory = categoryRepository.save(Category.builder().name("Sneakers").parentCategory(shoesCategory).build());
        Category bootsCategory = categoryRepository.save(Category.builder().name("Boots").parentCategory(shoesCategory).build()); // Boots 추가
        List<Category> categories = List.of(shoesCategory, sneakersCategory, bootsCategory);
        // 컬렉션 생성
        Collection jordanCollection = collectionRepository.save(Collection.builder().name("Jordan").build());
        List<Collection> collections = List.of(jordanCollection);

        // 상품 생성
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Product product = Product.builder()
                    .name("Product " + i)
                    .englishName("English Product " + i)
                    .releasePrice(100 + i * 50)
                    .modelNumber("Model" + i)
                    .releaseDate("2023-01-0" + i)
                    .gender(GenderType.UNISEX)
                    .brand(randomBrand(brands))
                    .category(sneakersCategory)
                    .collection(jordanCollection)
                    .build();

            Product savedProduct = productRepository.save(product);
            products.add(savedProduct);

            // 색상 생성
            for (int j = 0; j < 3; j++) {
                ProductColor productColor = ProductColor.builder()
                        .colorName(randomColor())
                        .product(savedProduct)
                        .build();

                ProductColor savedProductColor = productColorRepository.save(productColor);

                // 사이즈 생성
                List<String> randomSizes = randomSizes(SizeType.SHOES.getSizes(), 3);
                for (String size : randomSizes) {
                    productSizeRepository.save(ProductSize.builder()
                            .size(size)
                            .sizeType(SizeType.SHOES)
                            .purchasePrice(savedProduct.getReleasePrice())
                            .salePrice(savedProduct.getReleasePrice() + 20)
                            .quantity(10)
                            .productColor(savedProductColor)
                            .build());
                }
            }
        }

        return new TestData(brands, categories, collections, products);
    }

    private Brand randomBrand(List<Brand> brands) {
        return brands.get(random.nextInt(brands.size()));
    }

    private String randomColor() {
        ColorType[] colors = ColorType.values();
        return colors[random.nextInt(colors.length)].getDisplayName();
    }

    private List<String> randomSizes(String[] sizes, int count) {
        List<String> sizeList = Arrays.asList(sizes);
        return random.ints(0, sizeList.size())
                .distinct()
                .limit(count)
                .mapToObj(sizeList::get)
                .collect(Collectors.toList());
    }

    @Getter
    public static class TestData {
        private final List<Brand> brands;
        private final List<Category> categories;
        private final List<Collection> collections;
        private final List<Product> products;

        public TestData(List<Brand> brands, List<Category> categories, List<Collection> collections, List<Product> products) {
            this.brands = brands;
            this.categories = categories;
            this.collections = collections;
            this.products = products;
        }
    }
}
