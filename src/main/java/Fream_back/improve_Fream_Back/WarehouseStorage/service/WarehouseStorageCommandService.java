package Fream_back.improve_Fream_Back.WarehouseStorage.service;

import Fream_back.improve_Fream_Back.WarehouseStorage.entity.WarehouseStatus;
import Fream_back.improve_Fream_Back.WarehouseStorage.entity.WarehouseStorage;
import Fream_back.improve_Fream_Back.WarehouseStorage.repository.WarehouseStorageRepository;
import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.user.entity.User;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseStorageCommandService {

    private final WarehouseStorageRepository warehouseStorageRepository;

    public WarehouseStorage createOrderStorage(Order order, User user) {
        WarehouseStorage warehouseStorage = WarehouseStorage.builder()
                .user(user)
                .order(order)
                .storageLocation("Default Location") // 기본 창고 위치
                .status(WarehouseStatus.IN_STORAGE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1)) // 기본 1개월 보관
                .build();

        return warehouseStorageRepository.save(warehouseStorage);
    }
}
