//package Fream_back.improve_Fream_Back.shipment.service;
//
//import Fream_back.improve_Fream_Back.order.entity.Order;
//import Fream_back.improve_Fream_Back.shipment.dto.ShipmentResponseDto;
//import Fream_back.improve_Fream_Back.shipment.dto.ShipmentUpdateRequestDto;
//import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
//import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
//import Fream_back.improve_Fream_Back.shipment.repository.ShipmentRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//
//@Service
//@RequiredArgsConstructor
//public class ShipmentService {
//
//    private final ShipmentRepository shipmentRepository;
//
//    /**
//     * 배송 생성
//     */
//    @Transactional
//    public void createShipment(Order order) {
//        Shipment shipment = Shipment.builder()
//                .order(order)
//                .shipmentStatus(ShipmentStatus.PENDING) // 초기 상태: 배송 준비 중
//                .build();
//
//        shipmentRepository.save(shipment);
//    }
//
//    /**
//     * 송장 등록 및 배송 시작
//     */
//    @Transactional
//    public void registerTrackingInfo(Long shipmentId, String trackingNumber, String courierCompany) {
//        Shipment shipment = shipmentRepository.findById(shipmentId)
//                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
//
//        shipment.registerTrackingInfo(trackingNumber, courierCompany);
//        shipment.updateShipmentStatus(ShipmentStatus.SHIPPED);
//    }
//
//    /**
//     * 배송 상태 업데이트
//     */
//    @Transactional
//    public void updateShipmentStatus(Long shipmentId, ShipmentUpdateRequestDto requestDto) {
//        Shipment shipment = shipmentRepository.findById(shipmentId)
//                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
//
//        // 배송 상태 업데이트
//        shipment.updateShipmentStatus(requestDto.getShipmentStatus());
//
//        // 운송장 번호와 택배사 정보가 제공되었을 경우 업데이트
//        if (requestDto.getTrackingNumber() != null && requestDto.getCourierCompany() != null) {
//            shipment.registerTrackingInfo(requestDto.getTrackingNumber(), requestDto.getCourierCompany());
//        }
//    }
//
//    /**
//     * 배송 환불 처리 (배송 준비 중일 경우 즉시 처리, 배송 중일 경우 접수 상태로 변경)
//     */
//    @Transactional
//    public void processRefund(Long shipmentId) {
//        Shipment shipment = shipmentRepository.findById(shipmentId)
//                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
//
//        if (shipment.getShipmentStatus() == ShipmentStatus.PENDING) {
//            shipment.updateShipmentStatus(ShipmentStatus.CANCELED);
//        } else if (shipment.getShipmentStatus() == ShipmentStatus.SHIPPED || shipment.getShipmentStatus() == ShipmentStatus.IN_TRANSIT) {
//            shipment.updateShipmentStatus(ShipmentStatus.RETURNED);
//        } else {
//            throw new IllegalStateException("현재 상태에서는 환불을 처리할 수 없습니다.");
//        }
//    }
//    @Transactional(readOnly = true)
//    public ShipmentResponseDto getShipmentDetailsByOrder(Long orderId) {
//        // Order에서 Shipment 조회
//        Shipment shipment = shipmentRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
//
//        // ShipmentResponseDto로 변환
//        return ShipmentResponseDto.builder()
//                .shipmentId(shipment.getId())
//                .shipmentStatus(shipment.getShipmentStatus().name())
//                .trackingNumber(shipment.getTrackingNumber())
//                .courierCompany(shipment.getCourierCompany())
//                .shippedAt(shipment.getShippedAt())
//                .deliveredAt(shipment.getDeliveredAt())
//                .build();
//    }
//}