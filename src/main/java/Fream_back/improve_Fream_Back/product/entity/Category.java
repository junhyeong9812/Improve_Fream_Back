package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 카테고리명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory; // 상위 카테고리 참조

    @Builder.Default
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subCategories = new ArrayList<>(); // 하위 카테고리 목록

    //연관관계 메소드
    public void addSubCategory(Category subCategory) {
        subCategories.add(subCategory);
        subCategory.assignParentCategory(this);
    }

    public void assignParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    // 업데이트 메서드
    public void updateName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public void updateParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }
}