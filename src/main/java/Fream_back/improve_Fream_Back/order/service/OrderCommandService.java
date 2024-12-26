package Fream_back.improve_Fream_Back.order.service;

public class OrderCommandService {
//    public Payment createPayment(Order order, PaymentRequestDto requestDto) {
//        Payment payment;
//
//        switch (requestDto.getPaymentType()) {
//            case "GENERAL":
//                payment = GeneralPayment.builder()
//                        .impUid(requestDto.getImpUid())
//                        .pgProvider(requestDto.getPgProvider())
//                        .receiptUrl(requestDto.getReceiptUrl())
//                        .buyerName(requestDto.getBuyerName())
//                        .buyerEmail(requestDto.getBuyerEmail())
//                        .status("PAID")
//                        .paidAmount(requestDto.getPaidAmount())
//                        .build();
//                break;
//            case "ACCOUNT":
//                payment = AccountPayment.builder()
//                        .bankName(requestDto.getBankName())
//                        .accountNumber(requestDto.getAccountNumber())
//                        .accountHolder(requestDto.getAccountHolder())
//                        .receiptRequested(requestDto.isReceiptRequested())
//                        .paidAmount(requestDto.getPaidAmount())
//                        .build();
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported payment type: " + requestDto.getPaymentType());
//        }
//
//        payment.assignOrder(order);
//        return paymentRepository.save(payment);
//    }
}
