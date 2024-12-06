package Fream_back.improve_Fream_Back.order.controller;

import Fream_back.improve_Fream_Back.order.dto.OrderCreateRequestDto;
import Fream_back.improve_Fream_Back.shipment.dto.ShipmentUpdateRequestDto;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // 회원가입 요청
        String signupRequest = """
            {
                "loginId": "testUser",
                "password": "password123",
                "nickname": "TestUser",
                "realName": "Test Real Name",
                "phoneNumber": "010-1234-5678",
                "email": "test@example.com"
            }
        """;
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isOk());

        // 로그인 요청 및 토큰 저장
        String loginRequest = """
            {
                "loginId": "testUser",
                "password": "password123"
            }
        """;
        String jwtToken = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 카테고리 데이터 생성 (상위/하위 카테고리 포함)
        String categoryRequest = """
        {
            "mainCategoryName": "Clothing",
            "subCategoryNames": ["T-Shirts", "Jeans"]
        }
    """;
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequest))
                .andExpect(status().isOk());

        // 임시 URL 생성 (더미 이미지 파일 전송)
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "dummy-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Dummy Image Content".getBytes()
        );
        String tempUrl1 = mockMvc.perform(multipart("/api/products/temporary-url")
                        .file(mockFile)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String tempUrl2 = mockMvc.perform(multipart("/api/products/temporary-url")
                        .file(mockFile)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Product 데이터 생성
        String productRequest = """
    {
        "name": "Test Product",
        "brand": "Test Brand",
        "mainCategoryId": 1,
        "subCategoryId": 2,
        "initialPrice": 100.00,
        "description": "Test Product Description",
        "releaseDate": "2024-12-01",
        "images": [
            {
                "imageName": "image1.jpg",
                "temp_Url": "%s",
                "imageUrl": null,
                "imageType": "JPEG",
                "isMainThumbnail": true
            },
            {
                "imageName": "image2.jpg",
                "temp_Url": "%s",
                "imageUrl": null,
                "imageType": "JPEG",
                "isMainThumbnail": false
            }
        ],
        "sizeAndColorQuantities": [
            {
                "sizeType": "CLOTHING",
                "clothingSizes": ["M", "L"],
                "shoeSizes": null,
                "colors": ["RED", "BLUE"],
                "quantity": 15
            }
        ]
    }
    """.formatted(tempUrl1, tempUrl2);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest))
                .andExpect(status().isOk());
        // Delivery 데이터 생성
        String deliveryRequest = """
            {
                "recipientName": "Test Recipient",
                "phoneNumber": "010-1234-5678",
                "address": "Test Address",
                "addressDetail": "Test Address Detail",
                "zipCode": "12345"
            }
        """;
        mockMvc.perform(post("/api/deliveries/add")
                        .header("Authorization", jwtToken)
                        .param("loginId", "testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deliveryRequest))
                .andExpect(status().isOk());

        // Order 생성 데이터 추가
        String orderRequest = """
        {
            "userId": 1,
            "deliveryId": 1,
            "orderItems": [
                {
                    "productId": 1,
                    "quantity": 2,
                    "price": 100.00
                }
            ],
            "delivery": {
                "recipientName": "Test Recipient",
                "phoneNumber": "010-1234-5678",
                "address": "Test Address",
                "addressDetail": "Test Address Detail",
                "zipCode": "12345"
            },
            "payment": {
                "paymentMethod": "Credit Card",
                "amount": 200.00
            }
        }
    """;

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("주문 생성 테스트")
    void createOrder() throws Exception {
        // Given
        String requestBody = """
            {
                "userId": 1,
                "deliveryId": 1,
                "orderItems": [
                    {
                        "productId": 1,
                        "quantity": 2,
                        "price": 100.00
                    }
                ],
                "delivery": {
                    "recipientName": "Test Recipient",
                    "phoneNumber": "010-1234-5678",
                    "address": "Test Address",
                    "addressDetail": "Test Address Detail",
                    "zipCode": "12345"
                },
                "payment": {
                    "paymentMethod": "Credit Card",
                    "amount": 200.00
                }
            }
            """;

        // When & Then
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientName").value("Test Recipient"))
                .andExpect(jsonPath("$.totalPrice").value(200.00));
    }

    @Test
    @DisplayName("주문 상세 조회 테스트")
    void getOrderDetails() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(get("/order/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.recipientName").value("Test Recipient"));
    }

    @Test
    @DisplayName("결제 완료 및 배송 생성 테스트")
    void completeOrderPayment() throws Exception {
        // Given
        Long orderId = 1L;
        String paymentMethod = "Credit Card";
        BigDecimal amount = BigDecimal.valueOf(200.0);

        // When & Then
        mockMvc.perform(post("/order/" + orderId + "/complete-payment")
                        .param("paymentMethod", paymentMethod)
                        .param("amount", amount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Order payment completed and shipment created."));
    }

    @Test
    @DisplayName("배송 상태 업데이트 테스트")
    void updateShipmentStatus() throws Exception {
        // Given
        Long orderId = 1L;
        ShipmentUpdateRequestDto requestDto = new ShipmentUpdateRequestDto(
                "TRACK123",                // 운송장 번호
                "Test Courier",            // 택배사 이름
                ShipmentStatus.SHIPPED     // 변경할 배송 상태
        );

        String requestBody = objectMapper.writeValueAsString(requestDto);

        // When & Then
        mockMvc.perform(put("/order/" + orderId + "/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Shipment status updated successfully."));
    }

    @Test
    @DisplayName("결제 환불 처리 테스트")
    void refundPayment() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(post("/order/" + orderId + "/refund"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment refunded successfully."));
    }

    @Test
    @DisplayName("특정 주문의 결제 정보 조회 테스트")
    void getPaymentDetails() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(get("/order/" + orderId + "/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentMethod").value("Credit Card"))
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    @DisplayName("특정 주문의 배송 정보 조회 테스트")
    void getShipmentDetails() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(get("/order/" + orderId + "/shipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentStatus").value("SHIPPED"))
                .andExpect(jsonPath("$.trackingNumber").value("TRACK123"))
                .andExpect(jsonPath("$.courierCompany").value("Test Courier"));
    }
}