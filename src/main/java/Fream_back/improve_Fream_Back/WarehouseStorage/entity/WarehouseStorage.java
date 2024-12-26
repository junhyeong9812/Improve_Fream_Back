package Fream_back.improve_Fream_Back.WarehouseStorage.entity;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.sale.entity.Sale;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class WarehouseStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 구매한 상품의 보관

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale; // 판매 중인 상품의 보관

    private String storageLocation; // 창고 위치 정보

    private LocalDate startDate; // 보관 시작 날짜
    private LocalDate endDate; // 보관 종료 날짜

    public void assignOrder(Order order) {
        this.order = order;
        this.sale = null; // 배타적 관계 설정
    }

    public void assignSale(Sale sale) {
        this.sale = sale;
        this.order = null; // 배타적 관계 설정
    }

    public void assignUser(User user) {
        this.user = user;
    }

    public void setStorageDates(LocalDate startDate, int initialPeriodDays) {
        this.startDate = startDate;
        this.endDate = startDate.plusDays(initialPeriodDays);
    }

    public void extendStorage(int additionalDays) {
        this.endDate = this.endDate.plusDays(additionalDays);
    }
}

