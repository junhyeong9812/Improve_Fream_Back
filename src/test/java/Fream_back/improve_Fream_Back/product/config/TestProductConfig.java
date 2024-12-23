package Fream_back.improve_Fream_Back.product.config;

import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.ColorType;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import Fream_back.improve_Fream_Back.product.repository.*;
import jakarta.persistence.EntityManager;
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
            ProductSizeRepository productSizeRepository,
            EntityManager entityManager,
            ProductImageRepository productImageRepository
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
//            entityManager.flush();

            products.add(savedProduct);

            String[] fixedColors = {"Red", "Blue", "Green"};
            String[] fixedSizes = {"270", "280", "290"};

            for (int j = 0; j < fixedColors.length; j++) {

                ProductColor productColor = ProductColor.builder()
                        .colorName(fixedColors[j])
                        .product(savedProduct)
                        .build();

// 먼저 ProductColor를 저장하여 ID 생성
                ProductColor savedProductColor = productColorRepository.save(productColor);
                savedProduct.addProductColor(savedProductColor);


// 대표 이미지 생성 후 저장
                ProductImage thumbnailImage = ProductImage.builder()
                        .imageUrl("https://example.com/images/" + fixedColors[j].toLowerCase() + ".jpg")
                        .productColor(savedProductColor) // 저장된 ProductColor를 참조
                        .build();
                productImageRepository.save(thumbnailImage); // 별도로 저장
                savedProductColor.addThumbnailImage(thumbnailImage);
                productColorRepository.save(savedProductColor); // 다시 저장하여 관계 반영



                for (String size : fixedSizes) {
                    ProductSize productSize = ProductSize.builder()
                            .size(size)
                            .sizeType(SizeType.SHOES)
                            .purchasePrice(savedProduct.getReleasePrice())
                            .salePrice(savedProduct.getReleasePrice() + 20)
                            .quantity(10)
                            .build();
                    savedProductColor.addProductSize(productSize);
                    productSizeRepository.save(productSize);
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
