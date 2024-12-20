package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Interest;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestConfig.class, TestQueryDslConfig.class}) // QueryDSL Config도 가져오기
class InterestRepositoryTest {

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Test
    @DisplayName("유저와 관심 상품 색상 관계 저장 및 조회 테스트")
    void saveAndFindInterest() {
        // Given
        User savedUser = userRepository.findByEmail("user1@example.com").orElseThrow();

        Product product = Product.builder()
                .name("Air Max")
                .englishName("Air Max 2023")
                .releasePrice(150)
                .modelNumber("AM2023")
                .releaseDate("2023-01-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductColor productColor = ProductColor.builder()
                .colorName("Red")
                .product(savedProduct)
                .build();
        ProductColor savedProductColor = productColorRepository.save(productColor);

        Interest interest = Interest.builder()
                .user(savedUser)
                .productColor(savedProductColor)
                .build();
        interest.assignUser(savedUser);
        interest.assignProductColor(savedProductColor);

        // When
        Interest savedInterest = interestRepository.save(interest);

        // Then
        Optional<Interest> result = interestRepository.findByUserAndProductColor(savedUser, savedProductColor);
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getEmail()).isEqualTo("user1@example.com");
        assertThat(result.get().getProductColor().getColorName()).isEqualTo("Red");
    }

    @Test
    @DisplayName("특정 유저의 모든 관심 상품 조회 테스트")
    void findAllByUserId() {
        // Given
        User savedUser = userRepository.findByEmail("user1@example.com").orElseThrow();

        Product product1 = Product.builder()
                .name("Air Max")
                .englishName("Air Max 2023")
                .releasePrice(150)
                .modelNumber("AM2023")
                .releaseDate("2023-01-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct1 = productRepository.save(product1);

        ProductColor productColor1 = ProductColor.builder()
                .colorName("Red")
                .product(savedProduct1)
                .build();
        ProductColor savedProductColor1 = productColorRepository.save(productColor1);

        Product product2 = Product.builder()
                .name("Air Force")
                .englishName("Air Force 1")
                .releasePrice(120)
                .modelNumber("AF1001")
                .releaseDate("2023-02-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct2 = productRepository.save(product2);

        ProductColor productColor2 = ProductColor.builder()
                .colorName("Blue")
                .product(savedProduct2)
                .build();
        ProductColor savedProductColor2 = productColorRepository.save(productColor2);

        Interest interest1 = Interest.builder()
                .user(savedUser)
                .productColor(savedProductColor1)
                .build();
        interest1.assignUser(savedUser);
        interest1.assignProductColor(savedProductColor1);
        interestRepository.save(interest1);

        Interest interest2 = Interest.builder()
                .user(savedUser)
                .productColor(savedProductColor2)
                .build();
        interest2.assignUser(savedUser);
        interest2.assignProductColor(savedProductColor2);
        interestRepository.save(interest2);

        // When
        List<Interest> interests = interestRepository.findAllByUserId(savedUser.getId());

        // Then
        assertThat(interests).hasSize(2);
        assertThat(interests.get(0).getProductColor().getColorName()).isEqualTo("Red");
        assertThat(interests.get(1).getProductColor().getColorName()).isEqualTo("Blue");
    }

    @Test
    @DisplayName("관심 상품 삭제 테스트")
    void deleteInterest() {
        // Given
        User savedUser = userRepository.findByEmail("user1@example.com").orElseThrow();

        Product product = Product.builder()
                .name("Air Max")
                .englishName("Air Max 2023")
                .releasePrice(150)
                .modelNumber("AM2023")
                .releaseDate("2023-01-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductColor productColor = ProductColor.builder()
                .colorName("Red")
                .product(savedProduct)
                .build();
        ProductColor savedProductColor = productColorRepository.save(productColor);

        Interest interest = Interest.builder()
                .user(savedUser)
                .productColor(savedProductColor)
                .build();
        interest.assignUser(savedUser);
        interest.assignProductColor(savedProductColor);
        Interest savedInterest = interestRepository.save(interest);

        // When
        savedInterest.unassignUser();
        savedInterest.unassignProductColor();
        interestRepository.delete(savedInterest);
        interestRepository.flush();

        // Then
        Optional<Interest> result = interestRepository.findByUserAndProductColor(savedUser, savedProductColor);
        assertThat(result).isEmpty();
    }
}
