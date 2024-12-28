package Fream_back.improve_Fream_Back.shipment.service;

import Fream_back.improve_Fream_Back.WarehouseStorage.service.WarehouseStorageCommandService;
import Fream_back.improve_Fream_Back.sale.entity.Sale;
import Fream_back.improve_Fream_Back.sale.entity.SaleStatus;
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
    private final WarehouseStorageCommandService warehouseStorageCommandService;

    @Transactional
    public SellerShipment createSellerShipment(Long saleId, String courier, String trackingNumber) {
        // Sale 조회
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Sale을 찾을 수 없습니다: " + saleId));

        // SellerShipment 생성
        SellerShipment shipment = SellerShipment.builder()
                .sale(sale)
                .courier(courier)
                .trackingNumber(trackingNumber)
                .status(ShipmentStatus.IN_TRANSIT) // 배송 중 상태로 설정
                .build();

        // 연관관계 설정
        sale.assignSellerShipment(shipment);

        sellerShipmentRepository.save(shipment);
        if (sale.isWarehouseStorage()) {
            // 창고 보관 데이터 생성
            warehouseStorageCommandService.createSellerStorage(sale, sale.getSeller());
            sale.updateStatus(SaleStatus.IN_STORAGE); // 창고 보관 상태 업데이트
        } else {
            sale.updateStatus(SaleStatus.IN_TRANSIT); // 배송 중 상태 업데이트
        }
        return shipment;
    }

    @Transactional
    public SellerShipment updateShipment(Long shipmentId, String courier, String trackingNumber) {
        SellerShipment sellerShipment = sellerShipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Shipment를 찾을 수 없습니다."));

        sellerShipment.updateTrackingInfo(courier, trackingNumber);

        return sellerShipment;
    }
}
