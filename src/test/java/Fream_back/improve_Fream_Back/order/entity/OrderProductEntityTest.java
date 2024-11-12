package Fream_back.improve_Fream_Back.order.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderProductEntityTest {

    @Test
    @DisplayName("OrderProduct 엔티티 생성 테스트")
    public void createOrderProduct() {
        // OrderProduct 엔티티 생성
        OrderProduct orderProduct = OrderProduct.builder()
                .quantity(3)
                .build();

        // 데이터 검증
        assertThat(orderProduct.getQuantity()).isEqualTo(3);
    }
}
