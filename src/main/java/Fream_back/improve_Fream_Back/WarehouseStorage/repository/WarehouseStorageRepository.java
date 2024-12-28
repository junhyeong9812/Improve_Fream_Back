package Fream_back.improve_Fream_Back.WarehouseStorage.repository;

import Fream_back.improve_Fream_Back.WarehouseStorage.entity.WarehouseStorage;
import Fream_back.improve_Fream_Back.sale.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarehouseStorageRepository extends JpaRepository<WarehouseStorage, Long> {
    Optional<WarehouseStorage> findBySale(Sale sale);
    List<WarehouseStorage> findByUser_Email(String email);
}
