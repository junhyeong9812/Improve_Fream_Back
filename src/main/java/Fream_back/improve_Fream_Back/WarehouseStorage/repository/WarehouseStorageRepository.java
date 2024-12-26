package Fream_back.improve_Fream_Back.WarehouseStorage.repository;

import Fream_back.improve_Fream_Back.WarehouseStorage.entity.WarehouseStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseStorageRepository extends JpaRepository<WarehouseStorage, Long> {

    List<WarehouseStorage> findByUser_Email(String email);
}
