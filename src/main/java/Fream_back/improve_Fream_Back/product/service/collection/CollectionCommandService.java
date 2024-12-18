package Fream_back.improve_Fream_Back.product.service.collection;

import Fream_back.improve_Fream_Back.product.dto.CollectionRequestDto;
import Fream_back.improve_Fream_Back.product.dto.CollectionResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Collection;
import Fream_back.improve_Fream_Back.product.repository.CollectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CollectionCommandService {

    private final CollectionRepository collectionRepository;

    public CollectionCommandService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public CollectionResponseDto createCollection(CollectionRequestDto request) {
        Collection collection = Collection.builder()
                .name(request.getName())
                .build();
        collectionRepository.save(collection);
        return CollectionResponseDto.fromEntity(collection);
    }

    public CollectionResponseDto updateCollection(Long id, CollectionRequestDto request) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컬렉션입니다."));

        // 더티체크를 위한 업데이트
        collection.updateName(request.getName());
        return CollectionResponseDto.fromEntity(collection);
    }

    public void deleteCollection(String name) {
        Collection collection = collectionRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컬렉션입니다."));
        collectionRepository.delete(collection);
    }
}
