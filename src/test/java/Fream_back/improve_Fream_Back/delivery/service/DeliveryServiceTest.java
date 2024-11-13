package Fream_back.improve_Fream_Back.delivery.service;

import Fream_back.improve_Fream_Back.delivery.dto.DeliveryDto;
import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.delivery.repository.DeliveryRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private Delivery testDelivery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("testLoginId", "testPassword", "testNickname", "testName", "123-456-7890", "test@example.com", true, true, null);
        testDelivery = Delivery.builder()
                .id(1L)
                .user(testUser)
                .recipientName("John Doe")
                .phoneNumber("123-456-7890")
                .address("123 Test Street")
                .addressDetail("Apt 456")
                .zipCode("12345")
                .isDefault(true)
                .build();
    }

    @Test
    @DisplayName("배송지 추가 성공 테스트")
    void addDelivery_Success() {
        // given: 테스트를 위한 Mock 데이터 설정
        when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(testUser));
        when(deliveryRepository.countByUserId(anyLong())).thenReturn(4L);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(testDelivery);

        DeliveryDto dto = new DeliveryDto(null, "Jane Doe", "987-654-3210", "456 Another Street", "Unit 789", "54321", true);

        // when: 배송지 추가 서비스 호출
        String result = deliveryService.addDelivery("testLoginId", dto);

        // then: 결과 검증
        assertThat(result).isEqualTo("배송지가 성공적으로 추가되었습니다.");
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    @DisplayName("배송지 추가 실패 테스트 - 최대 제한 초과")
    void addDelivery_Fail_MaxLimit() {
        // given: 사용자와 5개의 배송지 Mock 데이터 설정
        when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(testUser));
        when(deliveryRepository.countByUserId(testUser.getId())).thenReturn(5L); // 이미 5개가 저장된 상태로 가정

        // 배송지 정보 설정
        DeliveryDto dto = new DeliveryDto(null, "New User", "555-555-5555", "New Address", "New Detail", "55555", true);

        // when: 추가 시도
        String result = deliveryService.addDelivery("testLoginId", dto);

        // then: "더이상 등록할 수 없습니다." 메시지가 반환되는지 검증
        assertThat(result).isEqualTo("더이상 등록할 수 없습니다.");
        verify(deliveryRepository, times(0)).save(any(Delivery.class));
    }

    @Test
    @DisplayName("배송지 목록 조회 테스트")
    void getDeliveries() {
        // given: 테스트를 위한 Mock 데이터 설정
        when(userRepository.findByLoginId("testLoginId")).thenReturn(Optional.of(testUser));
        when(deliveryRepository.findAllByUserIdWithFetchJoin(testUser.getId())).thenReturn(List.of(testDelivery));

        // when: 배송지 목록 조회 서비스 호출
        List<DeliveryDto> deliveries = deliveryService.getDeliveries("testLoginId");

        // then: 결과 검증
        assertThat(deliveries).isNotEmpty();
        assertThat(deliveries.get(0).getRecipientName()).isEqualTo("John Doe");
        assertThat(deliveries.get(0).isDefault()).isTrue();
    }

    @Test
    @DisplayName("배송지 업데이트 성공 테스트")
    void updateDelivery_Success() {
        // given: 테스트를 위한 Mock 데이터 설정
        when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(testUser));
        when(deliveryRepository.findById(anyLong())).thenReturn(Optional.of(testDelivery));

        DeliveryDto dto = new DeliveryDto(1L, "Jane Doe", "987-654-3210", "456 Another Street", "Unit 789", "54321", true);

        // when: 배송지 업데이트 서비스 호출
        String result = deliveryService.updateDelivery("testLoginId", dto);

        // then: 결과 검증
        assertThat(result).isEqualTo("배송지 정보가 성공적으로 수정되었습니다.");
        assertThat(testDelivery.getRecipientName()).isEqualTo("Jane Doe");
        assertThat(testDelivery.getPhoneNumber()).isEqualTo("987-654-3210");
    }

    @Test
    @DisplayName("배송지 삭제 성공 테스트")
    void deleteDelivery_Success() {
        // given: 테스트를 위한 Mock 데이터 설정
        when(userRepository.findByLoginId(anyString())).thenReturn(Optional.of(testUser));
        when(deliveryRepository.findById(anyLong())).thenReturn(Optional.of(testDelivery));

        DeliveryDto dto = new DeliveryDto(1L, "John Doe", "123-456-7890", "123 Test Street", "Apt 456", "12345", false);

        // when: 배송지 삭제 서비스 호출
        String result = deliveryService.deleteDelivery("testLoginId", dto);

        // then: 결과 검증
        assertThat(result).isEqualTo("배송지가 성공적으로 삭제되었습니다.");
        verify(deliveryRepository, times(1)).delete(any(Delivery.class));
    }

    @Test
    @DisplayName("기본 배송지 삭제 시 다음 배송지 기본으로 설정 테스트")
    void deleteDefaultDeliveryAndSetNextAsDefault() {
        // given: 사용자와 배송지 목록 설정
        // 기존에 @BeforeEach에서 설정된 testDelivery를 기본 배송지로 사용
        // testUser에 대한 배송지 목록은 testDelivery와 nextDelivery로 설정
        Delivery nextDelivery = new Delivery(2L, testUser, "Jane Doe", "987-654-3210", "456 Another Street", "Unit 789", "54321", false);
        deliveryRepository.save(nextDelivery);  // 배송지를 저장

        // deliveryRepository에 있는 배송지 목록을 Mock으로 설정
        when(userRepository.findByLoginId("testLoginId")).thenReturn(Optional.of(testUser));
        when(deliveryRepository.findById(testDelivery.getId())).thenReturn(Optional.of(testDelivery)); // 삭제할 기본 배송지
        when(deliveryRepository.findAllByUserId(testUser.getId()))
                .thenReturn(List.of(testDelivery, nextDelivery)); // 삭제 후 기본 배송지가 될 주소 포함

        // 기존 배송지 삭제 후 기본 배송지 변경을 위한 DTO
        DeliveryDto dto = new DeliveryDto(testDelivery.getId(), "John Doe", "123-456-7890", "123 Test Street", "Apt 456", "12345", true);

        // when: 기본 배송지 삭제 서비스 호출
        String result = deliveryService.deleteDelivery("testLoginId", dto);

        // then: 삭제 메시지 확인
        assertThat(result).isEqualTo("배송지가 성공적으로 삭제되었습니다.");

        // 기본 배송지가 삭제되었고, 그에 따라 다음 배송지가 기본 배송지로 설정되었는지 확인
        verify(deliveryRepository, times(1)).delete(testDelivery); // 기본 배송지 삭제 확인

        // `nextDelivery`의 기본 배송지 여부가 true로 설정되었는지 검증
        // 이 부분을 추가하여, 기본 배송지로 설정되었는지 실제로 확인합니다
        assertThat(nextDelivery.isDefault()).isTrue(); // 다음 배송지가 기본 배송지로 설정되었는지 확인
    }

//    @Test
//    @DisplayName("기본 배송지 삭제 시 다음 배송지 기본으로 설정 테스트")
//    void deleteDefaultDeliveryAndSetNextAsDefault2() {
//        // given: 사용자와 배송지 목록 설정
//        Delivery nextDelivery = new Delivery(2L, testUser, "Jane Doe", "987-654-3210", "456 Another Street", "Unit 789", "54321", false);
//        deliveryRepository.save(nextDelivery);  // 배송지 실제 저장
//
//        // 실제 DB에서 모든 배송지 반환하도록 설정 (Mock 사용 안 함)
//        List<Delivery> deliveries = deliveryRepository.findAllByUserId(testUser.getId());
//        when(deliveryRepository.findAllByUserId(testUser.getId()))
//                .thenReturn(deliveries);  // 실제로 저장된 배송지 목록 반환
//        // 기존 배송지 삭제 후 기본 배송지 변경을 위한 DTO
//        DeliveryDto dto = new DeliveryDto(testDelivery.getId(), "John Doe", "123-456-7890", "123 Test Street", "Apt 456", "12345", true);
//
//        // when: 기본 배송지 삭제 서비스 호출
//        String result = deliveryService.deleteDelivery("testLoginId", dto);
//
//        // then: 삭제 메시지 확인
//        assertThat(result).isEqualTo("배송지가 성공적으로 삭제되었습니다.");
//
//        // 기본 배송지가 삭제되었고, 그에 따라 다음 배송지가 기본 배송지로 설정되었는지 확인
//        verify(deliveryRepository, times(1)).delete(testDelivery); // 기본 배송지 삭제 확인
//        assertThat(nextDelivery.isDefault()).isTrue(); // 다음 배송지가 기본 배송지로 설정되었는지 확인
//    }



}
