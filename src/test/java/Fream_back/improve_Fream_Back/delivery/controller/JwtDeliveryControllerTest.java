package Fream_back.improve_Fream_Back.delivery.controller;


import Fream_back.improve_Fream_Back.user.dto.UserSignupDto;
import Fream_back.improve_Fream_Back.user.service.UserService;
import Fream_back.improve_Fream_Back.delivery.dto.DeliveryDto;
import Fream_back.improve_Fream_Back.delivery.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class JwtDeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc를 사용하여 REST API 요청 테스트

    @Autowired
    private UserService userService; // UserService를 통해 회원 가입 및 로그인 기능 호출

    @Autowired
    private DeliveryService deliveryService; // DeliveryService를 통해 배송지 관리 기능 호출

    private String token; // 로그인 후 얻은 JWT 토큰

    // 테스트 실행 전에 실제 회원 가입 및 로그인 진행하여 JWT 토큰을 얻음
    @BeforeEach
    public void setup() throws Exception {
        // Given: 유저 가입
        UserSignupDto userDto = new UserSignupDto(
                "testuser",           // loginId
                "password",           // password
                "Test User",          // nickname
                "Test User RealName", // realName
                "010-1234-5678",      // phoneNumber
                "testuser@example.com" // email
        );
        userService.registerUser(userDto); // 회원가입

        // Given: 로그인 요청 및 토큰 추출
        String loginPayload = "{\"loginId\":\"testuser\", \"password\":\"password\"}";
        token = mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization").substring(7); // JWT 토큰 추출
    }

    /**
     * 배송지 추가 테스트
     * Given: 유저가 로그인 후 배송지 추가 요청을 보낼 준비가 됨
     * When: 배송지 추가 요청을 보냄
     * Then: 배송지가 정상적으로 추가됨
     */
    @Test
    @DisplayName("배송지 추가 테스트")
    public void addDeliveryTest() throws Exception {
        // Given: 배송지 추가 요청을 위한 DTO
        DeliveryDto deliveryDto = new DeliveryDto("테스트 사용자", "010-1111-2222", "서울 강남구 테스팅 주소", "테스트 상세 주소", "12345", true);

        // When: 실제 배송지 추가 요청을 JWT 토큰을 사용하여 보냄
        mockMvc.perform(post("/api/jwt/deliveries/add")
                        .header("Authorization", "Bearer " + token) // 토큰 헤더에 추가
                        .contentType("application/json")
                        .content("{\"address\":\"서울 강남구 테스팅 주소\", \"phone\":\"010-1111-2222\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("배송지가 성공적으로 추가되었습니다."));

        // Then: 배송지 목록에 추가된 배송지가 존재하는지 확인
        mockMvc.perform(get("/api/jwt/deliveries/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("서울 강남구 테스팅 주소"));
    }

    /**
     * 배송지 목록 조회 테스트
     * Given: 로그인된 유저가 배송지 목록을 조회하려고 함
     * When: 배송지 목록 조회 요청을 보냄
     * Then: 로그인된 유저의 배송지 목록을 정상적으로 조회함
     */
    @Test
    @DisplayName("배송지 목록 조회 테스트")
    public void getDeliveriesTest() throws Exception {
        // Given: 이미 가입된 사용자와 배송지가 있어야 함
        DeliveryDto deliveryDto = new DeliveryDto("테스트 사용자", "010-1111-2222", "서울 강남구 테스팅 주소", "테스트 상세 주소", "12345", true);
        deliveryService.addDelivery("testuser", deliveryDto); // 배송지 추가

        // When: 배송지 목록을 조회 요청
        mockMvc.perform(get("/api/jwt/deliveries/list")
                        .header("Authorization", "Bearer " + token)) // 토큰 헤더에 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("서울 강남구 테스팅 주소"));

        // Then: 유저가 저장한 배송지 목록이 반환됨
    }

    /**
     * 배송지 수정 테스트
     * Given: 유저가 이미 등록된 배송지에 대해 수정을 요청함
     * When: 수정된 배송지 정보를 보냄
     * Then: 배송지 정보가 정상적으로 수정됨
     */
    @Test
    @DisplayName("배송지 수정 테스트")
    public void updateDeliveryTest() throws Exception {
        // Given: 기존 배송지 정보
        DeliveryDto deliveryDto = new DeliveryDto("테스트 사용자", "010-1111-2222", "서울 강남구 테스팅 주소", "테스트 상세 주소", "12345", true);

        // 배송지 추가
        mockMvc.perform(post("/api/jwt/deliveries/add")
                        .header("Authorization", "Bearer " + token) // 토큰 헤더에 추가
                        .contentType("application/json")
                        .content("{\"address\":\"서울 강남구 테스팅 주소\", \"phone\":\"010-1111-2222\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("배송지가 성공적으로 추가되었습니다."));

        // 배송지 목록 조회 후 id와 다른 데이터를 DTO로 매핑
        String response = mockMvc.perform(get("/api/jwt/deliveries/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("서울 강남구 테스팅 주소"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON 응답에서 첫 번째 배송지 데이터를 추출
//        DeliveryDto deliveryToUpdate = JsonPath.parse(response).read("$[0]", DeliveryDto.class);

        // When: 배송지 수정 요청을 보냄
        mockMvc.perform(put("/api/jwt/deliveries/update")
                        .header("Authorization", "Bearer " + token) // 토큰 헤더에 추가
                        .contentType("application/json")
                        .content("{\"id\": 1 , \"recipientName\": \"수정된 사용자\", \"phoneNumber\": \"010-2222-3333\", \"address\": \"서울 강남구 수정된 주소\", \"addressDetail\": \"수정된 상세 주소\", \"zipCode\": \"54321\", \"isDefault\": false}"))
                .andExpect(status().isOk())
                .andExpect(content().string("배송지 정보가 성공적으로 수정되었습니다."));

        // Then: 수정된 배송지 정보 확인
        mockMvc.perform(get("/api/jwt/deliveries/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("서울 강남구 수정된 주소"))
                .andExpect(jsonPath("$[0].phoneNumber").value("010-2222-3333"));
    }

    /**
     * 배송지 삭제 테스트
     * Given: 유저가 등록한 배송지를 삭제하려고 함
     * When: 배송지 삭제 요청을 보냄
     * Then: 배송지가 정상적으로 삭제됨
     */
    @Test
    @DisplayName("배송지 삭제 테스트")
    public void deleteDeliveryTest() throws Exception {
        // Given: 기존 배송지 정보
        DeliveryDto deliveryDto = new DeliveryDto("테스트 사용자", "010-1111-2222", "서울 강남구 테스팅 주소", "테스트 상세 주소", "12345", true);
        // 배송지 추가
        mockMvc.perform(post("/api/jwt/deliveries/add")
                        .header("Authorization", "Bearer " + token) // 토큰 헤더에 추가
                        .contentType("application/json")
                        .content("{\"address\":\"서울 강남구 테스팅 주소\", \"phone\":\"010-1111-2222\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("배송지가 성공적으로 추가되었습니다."));

        // 배송지 목록 조회 후 id와 다른 데이터를 DTO로 매핑
        String response = mockMvc.perform(get("/api/jwt/deliveries/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("서울 강남구 테스팅 주소"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON 응답에서 첫 번째 배송지 데이터를 추출
        DeliveryDto deliveryToDelete = JsonPath.parse(response).read("$[0]", DeliveryDto.class);

        // 배송지 삭제 요청
        mockMvc.perform(delete("/api/jwt/deliveries/delete")
                        .header("Authorization", "Bearer " + token) // 토큰 헤더에 포함
                        .contentType("application/json")
                        .content("{\"id\": 1, \"recipientName\": \"홍길동\", \"phoneNumber\": \"010-1234-5678\", \"address\": \"서울 강남구 테스팅 주소\", \"addressDetail\": \"테스트 상세 주소\", \"zipCode\": \"12345\", \"isDefault\": true}"))
                .andExpect(status().isOk())
                .andExpect(content().string("배송지가 성공적으로 삭제되었습니다."));

        // Then: 삭제된 배송지가 목록에서 보이지 않음을 확인
        mockMvc.perform(get("/api/jwt/deliveries/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("[]")); // 배송지 목록이 비어 있어야 함
    }
}
