package Fream_back.improve_Fream_Back.sale.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.shipment.entity.SellerShipment;
import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale  extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller; // 판매자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_size_id")
    private ProductSize productSize; // 판매 대상 상품 사이즈

    private String returnAddress; // 반송 주소
    private String postalCode; // 반송 우편번호
    private String receiverPhone; // 수령인 전화번호

    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment; // 판매 관련 결제 정보

    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private SaleBankAccount saleBankAccount;

    private boolean isWarehouseStorage; // 창고 보관 여부

    @Enumerated(EnumType.STRING)
    private SaleStatus status; // 판매 상태

    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private SaleBid saleBid; // Sale와 연결된 SaleBid

    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private SellerShipment sellerShipment;

    public void assignSaleBankAccount(SaleBankAccount saleBankAccount) {
        this.saleBankAccount = saleBankAccount;
        saleBankAccount.assignSale(this);
    }

    public void assignSaleBid(SaleBid saleBid) {
        this.saleBid = saleBid;
        saleBid.assignSale(this);
    }

    public void assignPayment(Payment payment) {
        this.payment = payment;
        payment.assignSale(this);
    }
    public void assignSellerShipment(SellerShipment sellerShipment) {
        this.sellerShipment = sellerShipment;
        sellerShipment.assignSale(this); // 연관관계 설정
        this.status = SaleStatus.IN_INSPECTION; // 상태를 검수 중으로 업데이트
    }

    public void updateStatus(SaleStatus newStatus) {
        this.status = newStatus;
    }
}
