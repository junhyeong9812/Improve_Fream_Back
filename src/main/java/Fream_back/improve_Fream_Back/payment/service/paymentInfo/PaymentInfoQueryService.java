package Fream_back.improve_Fream_Back.payment.service.paymentInfo;

import Fream_back.improve_Fream_Back.payment.dto.paymentInfo.PaymentInfoDto;
import Fream_back.improve_Fream_Back.payment.entity.PaymentInfo;
import Fream_back.improve_Fream_Back.payment.repository.PaymentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentInfoQueryService {

    private final PaymentInfoRepository paymentInfoRepository;

    @Transactional(readOnly = true)
    public List<PaymentInfoDto> getPaymentInfos(String email) {
        List<PaymentInfo> paymentInfos = paymentInfoRepository.findAllByUser_Email(email);

        return paymentInfos.stream()
                .map(pi -> new PaymentInfoDto(
                        pi.getId(),
                        pi.getCardNumber(),
                        pi.getCardPassword(),
                        pi.getExpirationDate(),
                        pi.getBirthDate()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentInfoDto getPaymentInfo(String email, Long id) {
        PaymentInfo paymentInfo = paymentInfoRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        return new PaymentInfoDto(
                paymentInfo.getId(),
                paymentInfo.getCardNumber(),
                paymentInfo.getCardPassword(),
                paymentInfo.getExpirationDate(),
                paymentInfo.getBirthDate());
    }
}
