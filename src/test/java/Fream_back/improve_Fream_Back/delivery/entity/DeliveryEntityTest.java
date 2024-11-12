package Fream_back.improve_Fream_Back.delivery.entity;

import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeliveryEntityTest {

    @Test
    @DisplayName("Delivery 엔티티 생성 테스트")
    public void createDelivery() {
        // Delivery 엔티티 생성
        Delivery delivery = Delivery.builder()
                .recipientName("Recipient")
                .phoneNumber("123-4567")
                .address("123 Main St")
                .addressDetail("Apt 101")
                .zipCode("12345")
                .isDefault(true)
                .build();

        // 데이터 검증
        assertThat(delivery.getRecipientName()).isEqualTo("Recipient");
        assertThat(delivery.getPhoneNumber()).isEqualTo("123-4567");
        assertThat(delivery.getAddress()).isEqualTo("123 Main St");
        assertThat(delivery.getAddressDetail()).isEqualTo("Apt 101");
        assertThat(delivery.getZipCode()).isEqualTo("12345");
        assertThat(delivery.isDefault()).isTrue();
    }
}
