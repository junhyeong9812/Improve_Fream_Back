package Fream_back.improve_Fream_Back.order.service;

import Fream_back.improve_Fream_Back.delivery.dto.DeliveryRequestDto;
import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.delivery.repository.DeliveryRepository;
import Fream_back.improve_Fream_Back.order.dto.*;
import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.payment.dto.PaymentRequestDto;
import Fream_back.improve_Fream_Back.payment.service.PaymentService;
import Fream_back.improve_Fream_Back.shipment.service.ShipmentService;
import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private ProductRepository productRepository;

    private User testUser;
    private Delivery testDelivery;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // User 데이터 생성
        testUser = User.builder()
                .id(1L)
                .loginId("testLogin")
                .password("encodedPassword")
                .nickname("TestUser")
                .realName("Test Real Name")
                .phoneNumber("010-1234-5678")
                .email("testuser@example.com")
                .phoneNotificationConsent(true)
                .emailNotificationConsent(false)
                .role(Role.USER)
                .build();

        // Delivery 데이터 생성
        testDelivery = Delivery.builder()
                .id(1L)
                .recipientName("Recipient Name")
                .user(testUser)
                .address("123 Test Street")
                .addressDetail("Apt 101")
                .build();

        // Product 데이터 생성
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .brand("Test Brand")
                .sku("SKU123")
                .initialPrice(BigDecimal.valueOf(100.0))
                .description("Test Product Description")
                .build();
    }

    @Test
    @DisplayName("주문 생성 테스트")
    void createOrder() {
        // Given
        OrderItemRequestDto orderItemDto = new OrderItemRequestDto(testProduct.getId(), 2, BigDecimal.valueOf(100.0));
        DeliveryRequestDto deliveryDto = new DeliveryRequestDto(
                "Recipient Name",
                "010-1234-5678",
                "123 Test Street",
                "Apt 101",
                "12345"
        );
        PaymentRequestDto paymentDto = new PaymentRequestDto("Credit Card", BigDecimal.valueOf(200.0));

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto();
        requestDto.setUserId(testUser.getId());
        requestDto.setDeliveryId(testDelivery.getId());
        requestDto.setOrderItems(List.of(orderItemDto));
        requestDto.setDelivery(deliveryDto);
        requestDto.setPayment(paymentDto);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(deliveryRepository.findById(testDelivery.getId())).thenReturn(Optional.of(testDelivery));
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrderResponseDto responseDto = orderService.createOrder(requestDto);

        // Then
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(deliveryRepository, times(1)).findById(testDelivery.getId());
        verify(productRepository, times(1)).findById(testProduct.getId());
        verify(orderRepository, times(1)).save(any(Order.class));

        assertThat(responseDto.getUserId()).isEqualTo(testUser.getId());
        assertThat(responseDto.getRecipientName()).isEqualTo(testDelivery.getRecipientName());
        assertThat(responseDto.getTotalPrice()).isEqualTo(BigDecimal.valueOf(200.0)); // 변경
    }

    @Test
    @DisplayName("주문 상세 조회 테스트")
    void getOrderDetails() {
        // Given
        Long orderId = 1L;

        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(2)
                .price(BigDecimal.valueOf(100.0))
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(testUser)
                .recipientName(testDelivery.getRecipientName())
                .address(testDelivery.getAddress())
                .addressDetail(testDelivery.getAddressDetail())
                .totalPrice(BigDecimal.valueOf(200.0)) // 변경
                .orderItems(List.of(orderItem))
                .build();

        when(orderRepository.findOrderDetailsById(orderId)).thenReturn(Optional.of(order));

        // When
        OrderResponseDto responseDto = orderService.getOrderDetails(orderId);

        // Then
        verify(orderRepository, times(1)).findOrderDetailsById(orderId);
        assertThat(responseDto.getOrderId()).isEqualTo(orderId);
        assertThat(responseDto.getRecipientName()).isEqualTo(testDelivery.getRecipientName());
        assertThat(responseDto.getTotalPrice()).isEqualTo(BigDecimal.valueOf(200.0)); // 변경
    }

    @Test
    @DisplayName("주문 취소 테스트")
    void cancelOrder() {
        // Given
        Long orderId = 1L;

        Order order = Order.builder()
                .id(orderId)
                .user(testUser)
                .paymentCompleted(false)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        orderService.cancelOrder(orderId);

        // Then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    @DisplayName("중복 결제 방지 테스트")
    void processPaymentAndShipment_duplicatePayment() {
        // Given
        Long orderId = 1L;
        String paymentMethod = "Credit Card";
        BigDecimal amount = BigDecimal.valueOf(200.0);

        // 이미 결제 완료된 주문
        Order order = Order.builder()
                .id(orderId)
                .user(testUser)
                .recipientName(testDelivery.getRecipientName())
                .address(testDelivery.getAddress())
                .addressDetail(testDelivery.getAddressDetail())
                .totalPrice(amount) // 변경
                .paymentCompleted(true) // 결제 완료 상태
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.processPaymentAndShipment(orderId, paymentMethod, amount)
        );

        assertThat(exception.getMessage()).isEqualTo("이미 결제가 완료된 주문입니다.");
        verify(orderRepository, times(1)).findById(orderId);
        verify(paymentService, never()).createPayment(any(Order.class), anyString(), any(BigDecimal.class));
        verify(paymentService, never()).markPaymentAsSuccessful(anyLong());
        verify(shipmentService, never()).createShipment(any(Order.class));
    }
}
