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

    // 연관관계 편의 메서드
    public void addSubCategory(SubCategory subCategory) {
        subCategories.add(subCategory);
        subCategory.setMainCategory(this);
    }
}
