package Fream_back.improve_Fream_Back.shipment.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.shipment.dto.ShipmentUpdateRequestDto;
import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import Fream_back.improve_Fream_Back.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShipmentServiceTest {

    @InjectMocks
    private ShipmentService shipmentService;

    @Mock
    private ShipmentRepository shipmentRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용 Order 및 OrderItem 생성
        OrderItem orderItem = OrderItem.builder()
                .quantity(1)
                .price(java.math.BigDecimal.valueOf(100.0))
                .build();

        testOrder = Order.builder()
                .id(1L)
                .recipientName("Test Recipient")
                .address("123 Test St")
                .addressDetail("Apt 101")
                .zipCode("12345")
                .paymentCompleted(false)
                .build();

        testOrder.getOrderItems().add(orderItem); // Order에 OrderItem 추가
    }

    @Test
    @DisplayName("배송 생성 테스트")
    void createShipment() {
        // Given
        Shipment shipment = Shipment.builder()
                .order(testOrder)
                .shipmentStatus(ShipmentStatus.PENDING)
                .build();

        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

        // When
        shipmentService.createShipment(testOrder);

        // Then
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
        assertThat(shipment.getShipmentStatus()).isEqualTo(ShipmentStatus.PENDING);
    }

    @Test
    @DisplayName("송장 등록 및 배송 시작 테스트")
    void registerTrackingInfo() {
        // Given
        Long shipmentId = 1L;
        Shipment shipment = Shipment.builder()
                .id(shipmentId)
                .order(testOrder)
                .shipmentStatus(ShipmentStatus.PENDING)
                .build();

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));

        // When
        shipmentService.registerTrackingInfo(shipmentId, "TRACK123", "FastDelivery");

        // Then
        assertThat(shipment.getTrackingNumber()).isEqualTo("TRACK123");
        assertThat(shipment.getCourierCompany()).isEqualTo("FastDelivery");
        assertThat(shipment.getShipmentStatus()).isEqualTo(ShipmentStatus.SHIPPED);
        verify(shipmentRepository, times(1)).findById(shipmentId);
    }

    @Test
    @DisplayName("배송 상태 업데이트 테스트")
    void updateShipmentStatus() {
        // Given
        Long shipmentId = 1L;
        Shipment shipment = Shipment.builder()
                .id(shipmentId)
                .shipmentStatus(ShipmentStatus.PENDING)
                .build();

        ShipmentUpdateRequestDto requestDto = new ShipmentUpdateRequestDto(
                "TRACK123", "FastDelivery", ShipmentStatus.IN_TRANSIT
        );

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));

        // When
        shipmentService.updateShipmentStatus(shipmentId, requestDto);

        // Then
        assertThat(shipment.getTrackingNumber()).isEqualTo("TRACK123");
        assertThat(shipment.getCourierCompany()).isEqualTo("FastDelivery");
        assertThat(shipment.getShipmentStatus()).isEqualTo(ShipmentStatus.IN_TRANSIT);
        verify(shipmentRepository, times(1)).findById(shipmentId);
    }

    @Test
    @DisplayName("배송 환불 처리 테스트")
    void processRefund() {
        // Given
        Long shipmentId = 1L;
        Shipment shipment = Shipment.builder()
                .id(shipmentId)
                .shipmentStatus(ShipmentStatus.PENDING)
                .build();

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));

        // When
        shipmentService.processRefund(shipmentId);

        // Then
        assertThat(shipment.getShipmentStatus()).isEqualTo(ShipmentStatus.CANCELED);
        verify(shipmentRepository, times(1)).findById(shipmentId);
    }

    @Test
    @DisplayName("배송 정보를 찾을 수 없는 경우 예외 처리")
    void shipmentNotFound() {
        // Given
        Long shipmentId = 999L;

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> shipmentService.registerTrackingInfo(shipmentId, "TRACK123", "FastDelivery"));
        verify(shipmentRepository, times(1)).findById(shipmentId);
    }
}