package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collection extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 컬렉션명

    // 업데이트 메서드
    public void updateName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }
}
