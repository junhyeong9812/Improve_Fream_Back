package Fream_back.improve_Fream_Back.product.service.collection;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.CollectionRequestDto;
import Fream_back.improve_Fream_Back.product.dto.CollectionResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Collection;
import Fream_back.improve_Fream_Back.product.repository.CollectionRepository;
import Fream_back.improve_Fream_Back.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestProductConfig.class)
@Transactional
class CollectionCommandServiceTest {

    @Autowired
    private CollectionCommandService collectionCommandService;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestProductConfig.TestData testData;

    @Test
    @DisplayName("컬렉션 생성 테스트")
    void createCollection() {
        // Given
        CollectionRequestDto request = new CollectionRequestDto("New Collection");

        // When
        CollectionResponseDto response = collectionCommandService.createCollection(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Collection");

        Collection savedCollection = collectionRepository.findByName("New Collection").orElseThrow();
        assertThat(savedCollection.getName()).isEqualTo("New Collection");
    }

    @Test
    @DisplayName("중복된 컬렉션 이름으로 생성 시 예외 발생")
    void createDuplicateCollection() {
        // Given
        CollectionRequestDto request = new CollectionRequestDto("Jordan"); // TestProductConfig에서 생성된 컬렉션 이름

        // When & Then
        assertThatThrownBy(() -> collectionCommandService.createCollection(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 컬렉션 이름입니다.");
    }

    @Test
    @DisplayName("컬렉션 업데이트 테스트")
    void updateCollection() {
        // Given
        Collection existingCollection = testData.getCollections().get(0); // Jordan
        CollectionRequestDto request = new CollectionRequestDto("Updated Collection");

        // When
        CollectionResponseDto response = collectionCommandService.updateCollection(existingCollection.getId(), request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Collection");

        Collection updatedCollection = collectionRepository.findById(existingCollection.getId()).orElseThrow();
        assertThat(updatedCollection.getName()).isEqualTo("Updated Collection");
    }

    @Test
    @DisplayName("상품이 포함된 컬렉션 삭제 시 예외 발생")
    void deleteCollectionWithProducts() {
        // Given
        Collection collectionWithProducts = testData.getCollections().get(0); // Jordan

        // When & Then
        assertThatThrownBy(() -> collectionCommandService.deleteCollection(collectionWithProducts.getName()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("해당 컬렉션과 연관된 상품이 존재합니다.");
    }

    @Test
    @DisplayName("컬렉션 삭제 테스트")
    void deleteCollection() {
        // Given
        Collection collection = collectionRepository.save(Collection.builder().name("Temporary Collection").build());

        // When
        collectionCommandService.deleteCollection("Temporary Collection");

        // Then
        assertThat(collectionRepository.findByName("Temporary Collection")).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 컬렉션 삭제 시 예외 발생")
    void deleteNonExistentCollection() {
        // When & Then
        assertThatThrownBy(() -> collectionCommandService.deleteCollection("NonExistent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 컬렉션입니다.");
    }
}
