package Fream_back.improve_Fream_Back.style.entity;

import Fream_back.improve_Fream_Back.order.entity.OrderItem;
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
public class StyleOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_id")
    private Style style;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    // 연관관계 메서드
    public void assignStyle(Style style) {
        this.style = style;
    }
    public void unassignStyle() {
        this.style = null;
    }

    public void assignOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}

