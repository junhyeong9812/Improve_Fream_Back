package Fream_back.improve_Fream_Back.address.repository;

import Fream_back.improve_Fream_Back.address.entity.Address;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByIdAndUser(Long id, User user);
}
