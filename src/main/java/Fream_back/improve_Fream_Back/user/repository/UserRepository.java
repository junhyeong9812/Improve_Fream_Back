package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
