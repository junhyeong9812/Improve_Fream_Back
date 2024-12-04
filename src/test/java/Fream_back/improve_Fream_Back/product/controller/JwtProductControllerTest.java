package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.dto.create.ProductCreateRequestDto;
import Fream_back.improve_Fream_Back.user.dto.UserSignupDto;
import Fream_back.improve_Fream_Back.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private String token;

    @BeforeEach
    public void setup() throws Exception {
        // 사용자 회원가입 및 로그인 처리
        UserSignupDto signupDto = new UserSignupDto(
                "testuser",
                "password",
                "Test User",
                "Test RealName",
                "010-1234-5678",
                "testuser@example.com"
        );
        userService.registerUser(signupDto);

        // 로그인 후 JWT 토큰 가져오기
        String loginPayload = "{\"loginId\": \"testuser\", \"password\": \"password\"}";
        token = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization")
                .substring(7); // JWT 토큰 추출
    }

    @Test
    @DisplayName("JWT 인증 후 상품 생성 테스트")
    public void createProductTest() throws Exception {
        // Given
        ProductCreateRequestDto productDto = ProductCreateRequestDto.builder()
                .name("Test Product")
                .brand("Test Brand")
                .sku("SKU12345")
                .mainCategoryId(1L)
                .subCategoryId(2L)
                .initialPrice(new BigDecimal("10000"))
                .description("This is a test product")
                .sizeAndColorQuantities(Set.of(
                        ProductSizeAndColorQuantityDto.builder()
                                .clothingSizes(Set.of("M", "L"))
                                .colors(Set.of("RED", "BLUE"))
                                .quantity(100)
                                .build()
                ))
                .build();

        String tempFilePaths = "[\"temp/path/1\", \"temp/path/2\"]";

        // When & Then
        mockMvc.perform(post("/api/jwt/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto))
                        .param("tempFilePaths", tempFilePaths))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("JWT 인증 후 상품 조회 테스트")
    public void getProductByIdTest() throws Exception {
        // Given
        Long productId = 1L; // Mock 데이터로 생성된 상품 ID

        // When & Then
        mockMvc.perform(get("/api/jwt/products/{productId}", productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @DisplayName("JWT 인증 후 상품 삭제 테스트")
    public void deleteProductTest() throws Exception {
        // Given
        Long productId = 1L; // Mock 데이터로 생성된 상품 ID

        // When & Then
        mockMvc.perform(delete("/api/jwt/products/{productId}", productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully."));
    }

    @Test
    @DisplayName("JWT 인증 후 필터링된 상품 조회 테스트")
    public void getFilteredProductsTest() throws Exception {
        // Given
        String filterParams = "?mainCategoryId=1&subCategoryId=2&color=RED&size=M&sortBy=priceAsc";

        // When & Then
        mockMvc.perform(get("/api/jwt/products/filter" + filterParams)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
