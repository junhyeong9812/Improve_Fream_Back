package Fream_back.improve_Fream_Back.style.repository;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.repository.OrderItemRepository;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class StyleRepositoryTest {

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private User testUser;
    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        // 1. 유저 생성
        testUser = User.builder()
                .loginId("testUser")
                .password("testPassword")
                .nickname("Tester")
                .realName("Test Real Name")
                .email("testuser@example.com")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);

        // 2. 주문 생성
        testOrder = Order.builder()
                .user(testUser)
                .recipientName("Recipient Name")
                .address("123 Test Street")
                .addressDetail("Apt 101")
                .totalPrice(BigDecimal.valueOf(200))
                .paymentCompleted(true) // 결제 완료 상태
                .build();
        orderRepository.save(testOrder);

        // 3. 주문 아이템 생성
        testOrderItem = OrderItem.builder()
                .order(testOrder)
                .price(BigDecimal.valueOf(100))
                .quantity(2)
                .build();
        orderItemRepository.save(testOrderItem);
    }

    @Test
    @DisplayName("스타일 저장 및 조회 테스트")
    void saveAndFindStyle() {
        // Given
        Style style = Style.builder()
                .user(testUser)
                .orderItem(testOrderItem)
                .content("Test Style Content")
                .rating(5)
                .imageUrl("testImage.jpg")
                .build();

        // When
        styleRepository.save(style);
        List<Style> styles = styleRepository.findAll();

        // Then
        assertThat(styles).hasSize(1);
        Style savedStyle = styles.get(0);
        assertThat(savedStyle.getContent()).isEqualTo("Test Style Content");
        assertThat(savedStyle.getRating()).isEqualTo(5);
        assertThat(savedStyle.getImageUrl()).isEqualTo("testImage.jpg");
    }

    @Test
    @DisplayName("특정 사용자와 연관된 스타일 조회 테스트")
    void findByUserId() {
        // Given
        Style style1 = Style.builder()
                .user(testUser)
                .orderItem(testOrderItem)
                .content("Style 1")
                .build();

        Style style2 = Style.builder()
                .user(testUser)
                .orderItem(testOrderItem)
                .content("Style 2")
                .build();

        styleRepository.save(style1);
        styleRepository.save(style2);

        // When
        List<Style> userStyles = styleRepository.findByUserId(testUser.getId());

        // Then
        assertThat(userStyles).hasSize(2);
        assertThat(userStyles.get(0).getContent()).isEqualTo("Style 1");
        assertThat(userStyles.get(1).getContent()).isEqualTo("Style 2");
    }

    @Test
    @DisplayName("특정 주문 상품에 대한 스타일 조회 테스트")
    void findByOrderItemId() {
        // Given
        Style style = Style.builder()
                .user(testUser)
                .orderItem(testOrderItem)
                .content("Test Style Content")
                .build();

        styleRepository.save(style);

        // When
        List<Style> styles = styleRepository.findByOrderItemId(testOrderItem.getId());

        // Then
        assertThat(styles).hasSize(1);
        assertThat(styles.get(0).getContent()).isEqualTo("Test Style Content");
    }

    @Test
    @DisplayName("특정 유저와 특정 상품에 대한 스타일 조회 테스트")
    void findByUserAndOrderItem() {
        // Given
        Style style = Style.builder()
                .user(testUser)
                .orderItem(testOrderItem)
                .content("Unique Style Content")
                .build();

        styleRepository.save(style);

        // When
        List<Style> styles = styleRepository.findByUserAndOrderItem(testUser.getId(), testOrderItem.getId());

        // Then
        assertThat(styles).hasSize(1);
        assertThat(styles.get(0).getContent()).isEqualTo("Unique Style Content");
    }

    @Test
    @DisplayName("스타일 삭제 테스트")
    void deleteStyle() {
        // Given
        Style style = Style.builder()
                .user(testUser)
                .orderItem(testOrderItem)
                .content("Style to Delete")
                .build();

        styleRepository.save(style);

        // When
        styleRepository.delete(style);
        List<Style> styles = styleRepository.findAll();

        // Then
        assertThat(styles).isEmpty();
    }
}