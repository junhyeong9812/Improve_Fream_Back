package Fream_back.improve_Fream_Back.delivery.controller;

import Fream_back.improve_Fream_Back.delivery.dto.DeliveryDto;
import Fream_back.improve_Fream_Back.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jwt/deliveries")
public class JwtDeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 배송지 추가 엔드포인트
     * 로그인된 사용자 정보를 SecurityContext에서 가져와 배송지 추가 요청을 처리합니다.
     * 기존 배송지가 5개를 초과할 경우 추가를 제한합니다.
     *
     * @param deliveryDto 배송지 정보가 담긴 DTO
     * @return ResponseEntity<String> 배송지 추가 성공 또는 제한 초과 메시지 반환
     */
    @PostMapping("/add")
    public ResponseEntity<String> addDelivery(@RequestBody DeliveryDto deliveryDto) {
        String loginId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = deliveryService.addDelivery(loginId, deliveryDto);
        return ResponseEntity.ok(result);
    }

    /**
     * 배송지 목록 조회 엔드포인트
     * 로그인된 사용자 정보를 SecurityContext에서 가져와 해당 사용자의 모든 배송지 목록을 조회합니다.
     *
     * @return ResponseEntity<List<DeliveryDto>> 조회된 배송지 목록을 담은 DTO 리스트 반환
     */
    @GetMapping("/list")
    public ResponseEntity<List<DeliveryDto>> getDeliveries() {
        String loginId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<DeliveryDto> deliveries = deliveryService.getDeliveries(loginId);
        return ResponseEntity.ok(deliveries);
    }

    /**
     * 배송지 정보 수정 엔드포인트
     * 로그인된 사용자 정보를 SecurityContext에서 가져와 지정된 배송지 ID에 대해 배송 정보를 업데이트합니다.
     * 기본 배송지로 설정하는 경우 다른 기본 배송지의 상태를 해제합니다.
     *
     * @param deliveryDto 수정할 배송지 정보가 담긴 DTO (id 필드 포함)
     * @return ResponseEntity<String> 배송지 수정 성공 메시지 반환
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateDelivery(@RequestBody DeliveryDto deliveryDto) {
        String loginId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = deliveryService.updateDelivery(loginId, deliveryDto);
        return ResponseEntity.ok(result);
    }

    /**
     * 배송지 삭제 엔드포인트
     * 로그인된 사용자 정보를 SecurityContext에서 가져와 지정된 배송지 ID에 대해 삭제를 수행합니다.
     * 기본 배송지는 해제한 후 삭제가 가능합니다.
     *
     * @param deliveryDto 삭제할 배송지 정보가 담긴 DTO (id 필드 포함)
     * @return ResponseEntity<String> 삭제 성공 메시지 반환
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDelivery(@RequestBody DeliveryDto deliveryDto) {
        String loginId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            String result = deliveryService.deleteDelivery(loginId, deliveryDto);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
