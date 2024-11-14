package Fream_back.improve_Fream_Back.delivery.controller;

import Fream_back.improve_Fream_Back.delivery.dto.DeliveryDto;
import Fream_back.improve_Fream_Back.delivery.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService; // 실제 서비스 빈을 @MockBean으로 Mock 처리

    private String testLoginId;

    @BeforeEach
    void setup() {
        testLoginId = "testLoginId"; // 실제 로그인 ID로 변경 가능
    }

    @Test
    @DisplayName("배송지 추가 시 성공 메시지가 반환된다.")
    void addDelivery_ShouldReturnOk_WhenDeliveryAddedSuccessfully() throws Exception {
        // Given: 배송지 추가 요청에 필요한 DTO와 서비스 메서드 설정
        DeliveryDto deliveryDto = new DeliveryDto("홍길동", "01012345678", "서울시 강남구", "역삼동 123", "12345", true);
        when(deliveryService.addDelivery(any(String.class), any(DeliveryDto.class))).thenReturn("배송지 추가 성공");

        // When: 배송지 추가 API를 호출
        mockMvc.perform(post("/api/deliveries/add")
                        .param("loginId", testLoginId) // testLoginId를 직접 파라미터로 전달
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipientName\":\"홍길동\",\"phoneNumber\":\"01012345678\",\"address\":\"서울시 강남구\",\"addressDetail\":\"역삼동 123\",\"zipCode\":\"12345\",\"isDefault\":true}"))
                // Then: 성공적으로 배송지 추가 메시지가 반환된다.
                .andExpect(status().isOk())
                .andExpect(content().string("배송지 추가 성공"));
    }

    @Test
    @DisplayName("배송지 목록 조회 시 사용자 배송지 목록이 반환된다.")
    void getDeliveries_ShouldReturnList_WhenDeliveriesExist() throws Exception {
        // Given: 사용자의 배송지 목록 데이터를 설정
        DeliveryDto deliveryDto1 = new DeliveryDto(1L, "홍길동", "01012345678", "서울시 강남구", "역삼동 123", "12345", true);
        DeliveryDto deliveryDto2 = new DeliveryDto(2L, "김철수", "01098765432", "서울시 송파구", "잠실동 456", "12345", false);

        when(deliveryService.getDeliveries(any(String.class))).thenReturn(List.of(deliveryDto1, deliveryDto2));

        // When: 배송지 목록 조회 API를 호출
        mockMvc.perform(get("/api/deliveries/list")
                        .param("loginId", testLoginId)) // testLoginId를 파라미터로 전달
                // Then: 배송지 목록이 성공적으로 반환된다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipientName").value("홍길동"))
                .andExpect(jsonPath("$[1].recipientName").value("김철수"));
    }

    @Test
    @DisplayName("배송지 수정 시 성공 메시지가 반환된다.")
    void updateDelivery_ShouldReturnOk_WhenDeliveryUpdated() throws Exception {
        // Given: 수정할 배송지 정보를 설정
        DeliveryDto deliveryDto = new DeliveryDto(1L, "홍길동", "01012345678", "서울시 강남구", "역삼동 123", "12345", true);
        when(deliveryService.updateDelivery(any(String.class), any(DeliveryDto.class))).thenReturn("배송지 수정 성공");

        // When: 배송지 수정 API를 호출
        mockMvc.perform(put("/api/deliveries/update")
                        .param("loginId", testLoginId) // testLoginId를 파라미터로 전달
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"recipientName\":\"홍길동\",\"phoneNumber\":\"01012345678\",\"address\":\"서울시 강남구\",\"addressDetail\":\"역삼동 123\",\"zipCode\":\"12345\",\"isDefault\":true}"))
                // Then: 배송지 수정 성공 메시지가 반환된다.
                .andExpect(status().isOk())
                .andExpect(content().string("배송지 수정 성공"));
    }

    @Test
    @DisplayName("기본 배송지 삭제 시 실패하고 오류 메시지가 반환된다.")
    void deleteDelivery_ShouldReturnBadRequest_WhenDeletingDefaultDelivery() throws Exception {
        // Given: 기본 배송지를 삭제하려는 요청
        DeliveryDto deliveryDto = new DeliveryDto(1L, "홍길동", "01012345678", "서울시 강남구", "역삼동 123", "12345", true);
        when(deliveryService.deleteDelivery(any(String.class), any(DeliveryDto.class))).thenThrow(new IllegalStateException("기본 배송지는 삭제할 수 없습니다."));

        // When: 배송지 삭제 API를 호출
        mockMvc.perform(delete("/api/deliveries/delete")
                        .param("loginId", testLoginId) // testLoginId를 파라미터로 전달
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"recipientName\":\"홍길동\",\"phoneNumber\":\"01012345678\",\"address\":\"서울시 강남구\",\"addressDetail\":\"역삼동 123\",\"zipCode\":\"12345\",\"isDefault\":true}"))
                // Then: 기본 배송지 삭제는 불가능하고 오류 메시지가 반환된다.
                .andExpect(status().isBadRequest())
                .andExpect(content().string("기본 배송지는 삭제할 수 없습니다."));
    }
}
