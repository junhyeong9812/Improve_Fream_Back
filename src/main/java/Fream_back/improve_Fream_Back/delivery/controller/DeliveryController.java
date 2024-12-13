//package Fream_back.improve_Fream_Back.delivery.controller;
//
//import Fream_back.improve_Fream_Back.delivery.dto.DeliveryDto;
//import Fream_back.improve_Fream_Back.delivery.service.DeliveryService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/deliveries")
//public class DeliveryController {
//
//    @Autowired
//    private DeliveryService deliveryService;
//
//    /**
//     * 배송지 추가 엔드포인트
//     * 사용자가 배송지를 추가 요청할 때 전달된 loginId와 배송지 정보를 기반으로 새로운 배송지를 추가합니다.
//     * 기존 배송지가 5개를 초과할 경우 추가를 제한합니다.
//     *
//     * @param loginId   추가할 사용자의 loginId
//     * @param deliveryDto  배송지 정보가 담긴 DTO
//     * @return ResponseEntity<String> 배송지 추가 성공 또는 제한 초과 메시지 반환
//     */
//    @PostMapping("/add")
//    public ResponseEntity<String> addDelivery(
//            @RequestParam("loginId") String loginId,
//            @RequestBody DeliveryDto deliveryDto
//    ) {
//        String result = deliveryService.addDelivery(loginId, deliveryDto);
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * 배송지 목록 조회 엔드포인트
//     * 특정 사용자의 모든 배송지 목록을 조회하고, 기본 배송지가 최우선으로 오도록 정렬하여 반환합니다.
//     *
//     * @param loginId 조회할 사용자의 loginId
//     * @return ResponseEntity<List<DeliveryDto>> 조회된 배송지 목록을 담은 DTO 리스트 반환
//     */
//    @GetMapping("/list")
//    public ResponseEntity<List<DeliveryDto>> getDeliveries(@RequestParam("loginId") String loginId) {
//        List<DeliveryDto> deliveries = deliveryService.getDeliveries(loginId);
//        return ResponseEntity.ok(deliveries);
//    }
//
//    /**
//     * 배송지 정보 수정 엔드포인트
//     * 지정된 배송지 ID에 대해 배송 정보를 업데이트합니다.
//     * 기본 배송지로 설정하는 경우 다른 기본 배송지의 상태를 해제합니다.
//     *
//     * @param loginId 사용자의 loginId
//     * @param deliveryDto 수정할 배송지 정보가 담긴 DTO (id 필드 포함)
//     * @return ResponseEntity<String> 배송지 수정 성공 메시지 반환
//     */
//    @PutMapping("/update")
//    public ResponseEntity<String> updateDelivery(
//            @RequestParam("loginId") String loginId,
//            @RequestBody DeliveryDto deliveryDto
//    ) {
//        String result = deliveryService.updateDelivery(loginId, deliveryDto);
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * 배송지 삭제 엔드포인트
//     * 지정된 배송지 ID에 대해 삭제를 수행합니다.
//     * 기본 배송지는 해제한 후 삭제가 가능합니다.
//     *
//     * @param loginId 사용자의 loginId
//     * @param deliveryDto 삭제할 배송지 정보가 담긴 DTO (id 필드 포함)
//     * @return ResponseEntity<String> 삭제 성공 메시지 반환
//     */
//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteDelivery(
//            @RequestParam("loginId") String loginId,
//            @RequestBody DeliveryDto deliveryDto
//    ) {
//        try {
//            String result = deliveryService.deleteDelivery(loginId, deliveryDto);
//            return ResponseEntity.ok(result);
//        } catch (IllegalStateException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//}
