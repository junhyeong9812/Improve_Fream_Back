package Fream_back.improve_Fream_Back.product.service.collection;

import Fream_back.improve_Fream_Back.product.dto.CollectionResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Collection;
import Fream_back.improve_Fream_Back.product.repository.CollectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CollectionQueryService {

    private final CollectionRepository collectionRepository;

    public CollectionQueryService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public List<CollectionResponseDto> findAllCollections() {
        return collectionRepository.findAllByOrderByNameDesc()
                .stream()
                .map(CollectionResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}