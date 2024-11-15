package Fream_back.improve_Fream_Back.Category.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 상위 카테고리 이름 (예: 의류, 신발, 가방 등)

    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<SubCategory> subCategories = new HashSet<>(); // 하위 카테고리들

    /**
     * 상위 카테고리 이름을 수정하는 메서드.
     * 이름을 변경하면 dirty checking을 통해 자동으로 DB에 반영됩니다.
     *
     * @param newName 새로운 상위 카테고리 이름
     */
    public void updateName(String newName) {
        this.name = newName; // 이름 변경
    }

    /**
     * 하위 카테고리를 추가하는 메서드.
     * @param subCategory 추가할 하위 카테고리
     */
    public void addSubCategory(SubCategory subCategory) {
        subCategories.add(subCategory);
        subCategory.setMainCategory(this); // 연관관계 설정
    }

    /**
     * 하위 카테고리를 삭제하는 메서드.
     * @param subCategory 삭제할 하위 카테고리
     */
    public void removeSubCategory(SubCategory subCategory) {
        subCategories.remove(subCategory);
        subCategory.setMainCategory(null); // 연관관계 해제
    }
}
