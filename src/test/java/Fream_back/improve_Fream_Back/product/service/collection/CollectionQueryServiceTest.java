package Fream_back.improve_Fream_Back.product.service.collection;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.CollectionResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Collection;
import Fream_back.improve_Fream_Back.product.repository.CollectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestProductConfig.class)
@Transactional
class CollectionQueryServiceTest {

    @Autowired
    private CollectionQueryService collectionQueryService;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private TestProductConfig.TestData testData;

    @Test
    @DisplayName("컬렉션 목록 조회 테스트")
    void findAllCollections() {
        // When
        List<CollectionResponseDto> collections = collectionQueryService.findAllCollections();

        // Then
        assertThat(collections).isNotNull();
        assertThat(collections).isNotEmpty();
        assertThat(collections.size()).isEqualTo(testData.getCollections().size());

        // Ensure the collections are ordered by name descending
        List<String> sortedNames = collections.stream().map(CollectionResponseDto::getName).toList();
        assertThat(sortedNames).isSortedAccordingTo((o1, o2) -> o2.compareTo(o1)); // Descending order check
    }

    @Test
    @DisplayName("컬렉션 이름 검증 테스트")
    void verifyCollectionNames() {
        // Given
        List<String> expectedNames = testData.getCollections().stream().map(Collection::getName).toList();

        // When
        List<CollectionResponseDto> collections = collectionQueryService.findAllCollections();

        // Then
        List<String> actualNames = collections.stream().map(CollectionResponseDto::getName).toList();
        assertThat(actualNames).containsExactlyInAnyOrderElementsOf(expectedNames);
    }
}
