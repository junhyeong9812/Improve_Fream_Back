package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

/**
 * UserProduct
 *
 * 사용자가 개별적으로 등록한 상품 정보를 관리하는 엔티티입니다.
 * 판매 가격, 상품 상태, 판매 가능 여부 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 등록 상품 ID (기본 키)

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 기본 상품 정보와 연결 (어떤 상품인지)

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller; // 이 상품을 판매하는 사용자 (판매자)

    private BigDecimal sellingPrice; // 개별 판매 가격
    private String condition; // 상품 상태 (e.g., 새 상품, 사용감 있음 등)
    private int quantity; // 판매 가능한 수량

    private boolean isAvailable; // 판매 가능 여부

    public UserProduct(Product product, User seller, BigDecimal sellingPrice, String condition, int quantity, boolean isAvailable) {
        this.product = product;
        this.seller = seller;
        this.sellingPrice = sellingPrice;
        this.condition = condition;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
    }

    // 판매 가능 여부 수정 메서드
    public void changeAvailability(boolean availability) {
        this.isAvailable = availability;
    }

    // 판매 가격 수정 메서드
    public void updateSellingPrice(BigDecimal newPrice) {
        this.sellingPrice = newPrice;
    }


    // 연관관계 편의 메서드 - Product 지정
    public void assignProduct(Product product) {
        this.product = product;
    }

    // 연관관계 편의 메서드 - Seller 지정
    public void assignSeller(User seller) {
        this.seller = seller;
    }
}
