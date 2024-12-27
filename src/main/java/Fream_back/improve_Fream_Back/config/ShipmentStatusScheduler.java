package Fream_back.improve_Fream_Back.config;

import Fream_back.improve_Fream_Back.shipment.service.OrderShipmentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentStatusScheduler {

    private final OrderShipmentCommandService orderShipmentCommandService;

    @Scheduled(cron = "0 0 */6 * * *") // 6시간마다 실행
    public void scheduleShipmentStatusUpdates() {
        orderShipmentCommandService.updateShipmentStatuses();
    }
}
