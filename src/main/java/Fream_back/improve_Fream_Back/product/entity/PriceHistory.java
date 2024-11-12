package Fream_back.improve_Fream_Back.product.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * PriceHistory
 *
 * 상품의 가격 변동 기록을 관리하는 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 가격 변동 기록 ID (기본 키)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 가격 변동이 발생한 상품

    // 가격 변동이 발생한 날짜는 createdDate를 활용
    private BigDecimal price; // 변동된 가격
    private String changeReason; // 가격 변동의 이유 (예: 할인, 수요 증가 등)

    // 연관관계 편의 메서드 - Product 지정
    public void assignProduct(Product product) {
        this.product = product;
    }
}
