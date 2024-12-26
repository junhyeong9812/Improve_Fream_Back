package Fream_back.improve_Fream_Back.payment.repository;

import Fream_back.improve_Fream_Back.payment.entity.CardPayment;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrder_User_EmailAndIsSuccess(String email, boolean isSuccess);
    List<Payment> findBySale_Seller_EmailAndIsSuccess(String email, boolean isSuccess);
    Optional<CardPayment> findByImpUid(String impUid);
}
