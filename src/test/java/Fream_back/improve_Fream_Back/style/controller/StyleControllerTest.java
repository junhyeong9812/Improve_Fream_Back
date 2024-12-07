package Fream_back.improve_Fream_Back.style.controller;

import Fream_back.improve_Fream_Back.delivery.dto.DeliveryDto;
import Fream_back.improve_Fream_Back.style.dto.StyleCreateDto;
import Fream_back.improve_Fream_Back.style.dto.StyleUpdateDto;
import Fream_back.improve_Fream_Back.style.service.styleFileUtil.StyleFileStorageUtil;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StyleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StyleFileStorageUtil styleFileStorageUtil;

    private String jwtToken;


    @BeforeEach
    void setUp() throws Exception {
        // 1. 유저 생성 및 로그인
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

        String loginRequest = """
            {
                "loginId": "testUser",
                "password": "password123"
            }
        """;
        jwtToken = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 2. 카테고리 생성
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

        // 3. 임시 파일 업로드
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
        System.out.println("tempUrl1 = " + tempUrl1);

        // 4. 프로덕트 생성
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
                        "imageType": "JPEG",
                        "isMainThumbnail": true
                    }
                ],
                "sizeAndColorQuantities": [
                    {
                        "sizeType": "CLOTHING",
                        "clothingSizes": ["M", "L"],
                        "colors": ["RED", "BLUE"],
                        "quantity": 15
                    }
                ]
            }
        """.formatted(tempUrl1);
        System.out.println("Formatted Product Request: " + productRequest);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest))
                .andExpect(status().isOk());

        // 5. 배송지 생성
        String deliveryRequest = """
    {
        "recipientName": "Test Recipient",
        "phoneNumber": "010-1234-5678",
        "address": "Test Address",
        "addressDetail": "Test Address Detail",
        "zipCode": "12345",
        "isDefault": true
    }
""";
        String deliveryResponse = mockMvc.perform(post("/api/deliveries/add")
                        .param("loginId", "testUser")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deliveryRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 6. 배송지 목록 조회
        String deliveryListResponse = mockMvc.perform(get("/api/deliveries/list")
                        .param("loginId", "testUser")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 배송지 목록에서 첫 번째 배송지 ID 추출
        List<DeliveryDto> deliveries = objectMapper.readValue(deliveryListResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, DeliveryDto.class));
        Long deliveryId = deliveries.get(0).getId(); // 첫 번째 배송지의 ID

        // 7. 주문 생성
        String orderRequest = """
    {
        "userId": 1,
        "deliveryId": %d,
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
""".formatted(deliveryId);

        mockMvc.perform(post("/order")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("임시 파일 업로드 테스트")
    void uploadTempFile_ShouldReturnTempFilePath() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "dummy-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Dummy Image Content".getBytes()
        );

        mockMvc.perform(multipart("/styles/upload-temp")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("styleTemp/")));

    }

    @Test
    @DisplayName("스타일 생성 테스트 - 임시 파일 업로드 경로 사용")
    void createStyle_ShouldReturnStyleId_UsingUploadedTempFile() throws Exception {
        // Step 1: 임시 파일 업로드
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "dummy-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Dummy Image Content".getBytes()
        );

        String tempFilePath = mockMvc.perform(multipart("/styles/upload-temp")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Step 2: 스타일 생성 요청
        StyleCreateDto createDto = new StyleCreateDto(
                1L, // User ID
                1L, // Order Item ID
                "Test Content",
                5,
                tempFilePath // 업로드된 임시 파일 경로 사용
        );

        mockMvc.perform(post("/styles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("1")); // Mocked Style ID
    }

    @Test
    @DisplayName("스타일 업데이트 테스트")
    void updateStyle_ShouldReturnUpdatedStyleId() throws Exception {
        StyleUpdateDto updateDto = new StyleUpdateDto(
                1L, // User ID
                "Updated Content",
                4,
                "temp/path/updated-file.jpg"
        );

        mockMvc.perform(put("/styles/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("1")); // Updated Style ID 확인
    }

    @Test
    @DisplayName("스타일 삭제 테스트")
    void deleteStyle_ShouldDeleteStyle() throws Exception {
        mockMvc.perform(delete("/styles/1/delete")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("스타일이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("스타일 상세 조회 테스트")
    void getStyleById_ShouldReturnStyleDetails() throws Exception {
        mockMvc.perform(get("/styles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    @DisplayName("스타일 목록 조회 테스트")
    void searchStyles_ShouldReturnPagedStyles() throws Exception {
        String searchDto = """
            {
                "userId": 1,
                "orderItemId": 1,
                "content": "Test"
            }
        """;

        mockMvc.perform(post("/styles/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(searchDto)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Test Content"));
    }

}