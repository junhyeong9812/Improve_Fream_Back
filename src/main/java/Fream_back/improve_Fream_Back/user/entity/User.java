package Fream_back.improve_Fream_Back.user.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseEntity;
import Fream_back.improve_Fream_Back.product.entity.UserProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * User
 *
 * 사용자 정보를 관리하는 엔티티입니다.
 * 사용자명, 비밀번호, 역할(Role) 등의 정보를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 ID (기본 키)

    private String username; // 사용자명
    private String password; // 비밀번호

    @Enumerated(EnumType.STRING)
    private Role role; // USER, ADMIN 등으로 역할 구분

    @OneToMany(mappedBy = "seller")
    private List<UserProduct> productsForSale; // 판매자로서 등록한 상품들

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // 연관관계 편의 메서드 - UserProduct 추가
    public void addProductForSale(UserProduct userProduct) {
        this.productsForSale.add(userProduct);
        userProduct.assignSeller(this);
    }


}
