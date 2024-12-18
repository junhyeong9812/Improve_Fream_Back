package Fream_back.improve_Fream_Back.product.dto;

import Fream_back.improve_Fream_Back.product.entity.Collection;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionResponseDto {
    private Long id; // 컬렉션 ID
    private String name; // 컬렉션명

    public static CollectionResponseDto fromEntity(Collection collection) {
        return CollectionResponseDto.builder()
                .id(collection.getId())
                .name(collection.getName())
                .build();
    }
}
