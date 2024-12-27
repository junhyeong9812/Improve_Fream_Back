package Fream_back.improve_Fream_Back.shipment.service;

import Fream_back.improve_Fream_Back.sale.entity.Sale;
import Fream_back.improve_Fream_Back.sale.repository.SaleRepository;
import Fream_back.improve_Fream_Back.shipment.entity.SellerShipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import Fream_back.improve_Fream_Back.shipment.repository.SellerShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerShipmentCommandService {

    private final SellerShipmentRepository sellerShipmentRepository;
    private final SaleRepository saleRepository;

    @Transactional
    public SellerShipment createSellerShipment(Long saleId, String courier, String trackingNumber) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Sale을 찾을 수 없습니다."));

        SellerShipment sellerShipment = SellerShipment.builder()
                .sale(sale)
                .courier(courier)
                .trackingNumber(trackingNumber)
                .status(ShipmentStatus.IN_TRANSIT) // 배송 중 상태
                .build();

        sale.assignSellerShipment(sellerShipment);

        return sellerShipmentRepository.save(sellerShipment);
    }

    @Transactional
    public SellerShipment updateShipment(Long shipmentId, String courier, String trackingNumber) {
        SellerShipment sellerShipment = sellerShipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Shipment를 찾을 수 없습니다."));

        sellerShipment.updateTrackingInfo(courier, trackingNumber);

        return sellerShipment;
    }
}
