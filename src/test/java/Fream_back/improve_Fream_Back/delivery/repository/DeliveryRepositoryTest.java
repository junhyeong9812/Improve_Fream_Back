package Fream_back.improve_Fream_Back.delivery.repository;

import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.delivery.repository.DeliveryRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DeliveryRepositoryTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Delivery 저장 및 조회 테스트")
    public void saveAndFindDelivery() {
        // User 생성 및 저장
        User user = User.builder()
                .username("testuser")
                .password("password")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // Delivery 생성 및 저장
        Delivery delivery = Delivery.builder()
                .user(user)
                .recipientName("Recipient")
                .phoneNumber("123-4567")
                .address("123 Main St")
                .addressDetail("Apt 101")
                .zipCode("12345")
                .isDefault(true)
                .build();
        deliveryRepository.save(delivery);

        // Delivery 및 연관 데이터 조회
        Delivery foundDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();
        assertThat(foundDelivery.getRecipientName()).isEqualTo("Recipient");
        assertThat(foundDelivery.getUser().getUsername()).isEqualTo("testuser");
        assertThat(foundDelivery.isDefault()).isTrue();
    }
}

