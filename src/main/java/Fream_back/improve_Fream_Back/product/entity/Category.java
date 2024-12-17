package Fream_back.improve_Fream_Back.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 카테고리명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory; // 상위 카테고리 참조

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
}
