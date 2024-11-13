package Fream_back.improve_Fream_Back.delivery.service;

import Fream_back.improve_Fream_Back.delivery.dto.DeliveryDto;
import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.delivery.repository.DeliveryRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 배송지 추가 서비스
     * 사용자의 loginId를 기반으로 해당 사용자를 조회하고, 배송지 개수를 확인하여 제한 초과 여부를 검사합니다.
     * 새로운 배송지를 기본 배송지로 설정하면 기존 기본 배송지는 자동으로 해제합니다.
     *
     * @param loginId  배송지를 추가할 사용자의 loginId
     * @param deliveryDto 배송지 추가에 필요한 정보가 담긴 DTO
     * @return String 배송지 추가 성공 또는 제한 초과 메시지
     */
    @Transactional
    public String addDelivery(String loginId, DeliveryDto deliveryDto) {
        // 주어진 loginId로 사용자 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 사용자별 배송지 개수 확인
        long deliveryCount = deliveryRepository.countByUserId(user.getId());

        if (deliveryCount >= 5) {
            return "더이상 등록할 수 없습니다.";
        }

        // 기본 배송지로 설정된 배송지가 이미 있는 경우 해제
        if (deliveryDto.isDefault()) {
            deliveryRepository.findAllByUserIdWithFetchJoin(user.getId()).stream()
                    .filter(Delivery::isDefault)
                    .forEach(existingDelivery -> existingDelivery.setAsDefault(false));
        }

        // 새로운 배송지 추가
        Delivery newDelivery = Delivery.builder()
                .user(user)
                .recipientName(deliveryDto.getRecipientName())
                .phoneNumber(deliveryDto.getPhoneNumber())
                .address(deliveryDto.getAddress())
                .addressDetail(deliveryDto.getAddressDetail())
                .zipCode(deliveryDto.getZipCode())
                .isDefault(deliveryDto.isDefault())
                .build();

        deliveryRepository.save(newDelivery);
        return "배송지가 성공적으로 추가되었습니다.";
    }

    /**
     * 배송지 목록 조회 서비스
     * 특정 사용자의 모든 배송지 정보를 조회하고, 기본 배송지를 최우선으로 정렬하여 반환합니다.
     *
     * @param loginId 조회할 사용자의 loginId
     * @return List<DeliveryDto> 조회된 배송지 목록을 담은 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<DeliveryDto> getDeliveries(String loginId) {
        // 주어진 loginId로 사용자 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 배송지 목록 조회 및 기본 배송지를 최우선으로 정렬하여 DTO로 변환
        List<Delivery> deliveries = deliveryRepository.findAllByUserIdWithFetchJoin(user.getId());
        return deliveries.stream()
                .sorted((d1, d2) -> Boolean.compare(d2.isDefault(), d1.isDefault())) // 기본 배송지를 최우선으로 정렬
                .map(DeliveryDto::fromEntity)
                .collect(Collectors.toList());
    }
    /**
     * 배송지 정보 수정 서비스
     * 주어진 DTO의 ID와 수정 정보를 사용하여 배송지를 업데이트하고, 기본 배송지 여부에 따라 다른 배송지의 기본 상태를 업데이트합니다.
     *
     * @param loginId 사용자의 loginId
     * @param deliveryDto 수정할 배송지 정보가 담긴 DTO (id 필드 포함)
     * @return String 수정 성공 또는 오류 메시지 반환
     */
    @Transactional
    public String updateDelivery(String loginId, DeliveryDto deliveryDto) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Delivery delivery = deliveryRepository.findById(deliveryDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        if (!delivery.getUser().equals(user)) {
            throw new IllegalArgumentException("Access denied for this delivery.");
        }

        if (deliveryDto.isDefault()) {
            deliveryRepository.findAllByUserIdWithFetchJoin(user.getId()).stream()
                    .filter(Delivery::isDefault)
                    .forEach(existingDelivery -> existingDelivery.setAsDefault(false));
        }

        delivery.updateDelivery(deliveryDto.getRecipientName(), deliveryDto.getPhoneNumber(),
                deliveryDto.getAddress(), deliveryDto.getAddressDetail(),
                deliveryDto.getZipCode(), deliveryDto.isDefault());

        return "배송지 정보가 성공적으로 수정되었습니다.";
    }

    /**
     * 배송지 삭제 서비스
     * 주어진 DTO의 ID를 기반으로 삭제 요청을 처리합니다. 기본 배송지일 경우 삭제 전 기본 설정을 해제해야 합니다.
     *
     * @param loginId 사용자의 loginId
     * @param deliveryDto 삭제할 배송지 정보가 담긴 DTO (id 필드 포함)
     * @return String 삭제 성공 또는 오류 메시지 반환
     */
    @Transactional
    public String deleteDelivery(String loginId, DeliveryDto deliveryDto) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Delivery delivery = deliveryRepository.findById(deliveryDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        if (!delivery.getUser().equals(user)) {
            throw new IllegalArgumentException("Access denied for this delivery.");
        }

        if (delivery.isDefault()) {
            // 기본 배송지 삭제 시 다른 배송지를 기본 배송지로 설정
            changeDefaultDelivery(user, delivery);
        }

        // 배송지 삭제
        deliveryRepository.delete(delivery);
        deliveryRepository.flush(); // 변경 사항 DB에 반영
        return "배송지가 성공적으로 삭제되었습니다.";
    }

    /**
     * 기본 배송지 변경 서비스
     * 주어진 사용자의 기본 배송지가 삭제될 경우, 다른 배송지를 기본 배송지로 설정합니다.
     *
     * @param user 기본 배송지를 변경할 사용자의 정보
     * @param currentDelivery 삭제된 배송지 (기본 배송지였던 배송지)
     *
     * 이 메소드는 사용자의 배송지 목록을 조회하여 기본 배송지가 삭제되었을 때,
     * 다른 배송지 중 하나를 기본 배송지로 설정합니다. 만약 다른 배송지가 없으면 기본 배송지로 설정할 수 없습니다.
     */
    @Transactional
    public void changeDefaultDelivery(User user, Delivery currentDelivery) {
        List<Delivery> deliveries = deliveryRepository.findAllByUserId(user.getId());

        if (deliveries.size() > 1) {
            // 다른 배송지 중 하나를 기본 배송지로 설정
            Delivery nextDefault = deliveries.stream()
                    .filter(d -> !d.getId().equals(currentDelivery.getId()))
                    .findFirst()
                    .orElse(null);

            if (nextDefault != null) {
                nextDefault.setAsDefault(true); // 기본 배송지로 설정 (dirty checking 자동 적용)
            }
        }
    }
}
