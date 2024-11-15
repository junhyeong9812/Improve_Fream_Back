package Fream_back.improve_Fream_Back.Category.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 하위 카테고리 이름 (예: 티셔츠, 후드티, 슬리퍼 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory; // 상위 카테고리

    public void setMainCategory(MainCategory mainCategory) {
        this.mainCategory = mainCategory;
    }
    /**
     * 하위 카테고리 이름을 수정하는 메서드.
     * 이름을 변경하면 dirty checking을 통해 자동으로 DB에 반영됩니다.
     *
     * @param newName 새로운 하위 카테고리 이름
     */
    public void updateName(String newName) {
        this.name = newName; // 이름 변경
    }

    /**
     * 상위 카테고리 변경을 처리하는 메서드.
     * @param newMainCategory 새로운 상위 카테고리
     */
    public void updateMainCategory(MainCategory newMainCategory) {
        this.mainCategory = newMainCategory; // 상위 카테고리 변경
    }
    /**
     * 하위 카테고리의 상위 카테고리를 변경하는 메서드
     * 기존 상위 카테고리와 연관관계를 제거하고 새로운 상위 카테고리와 연관관계를 설정합니다.
     *
     * @param newMainCategory 새로운 상위 카테고리
     */
    public void changeMainCategory(MainCategory newMainCategory) {
        if (this.mainCategory != null) {
            this.mainCategory.getSubCategories().remove(this); // 기존 상위 카테고리와 관계 제거
        }
        this.mainCategory = newMainCategory; // 새로운 상위 카테고리 설정
        if (newMainCategory != null) {
            newMainCategory.getSubCategories().add(this); // 새로운 상위 카테고리와 관계 추가
        }
    }

}
