package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Collection;
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
@Import(TestQueryDslConfig.class) // QueryDSL 설정이 필요하다면 추가
class CollectionRepositoryTest {

    @Autowired
    private CollectionRepository collectionRepository;

    @Test
    @DisplayName("컬렉션 저장 및 이름으로 조회")
    void saveAndFindByName() {
        // Given
        Collection collection = Collection.builder()
                .name("Spring Collection")
                .build();
        collectionRepository.save(collection);

        // When
        Optional<Collection> result = collectionRepository.findByName("Spring Collection");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Spring Collection");
    }

    @Test
    @DisplayName("컬렉션 목록 이름 내림차순 조회")
    void findAllByOrderByNameDesc() {
        // Given
        collectionRepository.save(Collection.builder().name("Summer Collection").build());
        collectionRepository.save(Collection.builder().name("Spring Collection").build());

        // When
        List<Collection> result = collectionRepository.findAllByOrderByNameDesc();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Summer Collection"); // 내림차순 확인
        assertThat(result.get(1).getName()).isEqualTo("Spring Collection");
    }

    @Test
    @DisplayName("컬렉션 업데이트 테스트")
    void updateCollectionName() {
        // Given
        Collection collection = Collection.builder()
                .name("Old Collection")
                .build();
        Collection savedCollection = collectionRepository.save(collection);

        // When
        savedCollection.updateName("Updated Collection");
        Collection updatedCollection = collectionRepository.save(savedCollection);

        // Then
        assertThat(updatedCollection.getName()).isEqualTo("Updated Collection");
    }

    @Test
    @DisplayName("컬렉션 삭제 테스트")
    void deleteCollection() {
        // Given
        Collection collection = Collection.builder()
                .name("To Be Deleted")
                .build();
        Collection savedCollection = collectionRepository.save(collection);

        // When
        collectionRepository.delete(savedCollection);
        Optional<Collection> result = collectionRepository.findByName("To Be Deleted");

        // Then
        assertThat(result).isEmpty();
    }
}
