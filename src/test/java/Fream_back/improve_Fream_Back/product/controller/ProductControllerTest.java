package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("임시 URL 생성 및 파일 존재 확인 테스트")
    public void createTemporaryUrlTest() throws Exception {
        // Given
        String originalFileName = "test-image.jpg";

        // When
        String responseContent = mockMvc.perform(multipart("/api/products/temporary-url")
                        .file("file", originalFileName.getBytes()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(); // 반환된 URL 가져오기

        // Then
        Path tempFilePath = Paths.get(responseContent); // 반환된 URL을 경로로 변환
        assertTrue(Files.exists(tempFilePath), "파일이 반환된 경로에 존재하지 않습니다.");

        // 테스트 종료 후 파일 삭제
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    @DisplayName("상품 생성 테스트")
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
        ProductIdResponseDto responseDto = new ProductIdResponseDto(1L);

        when(productService.createProduct(any(ProductCreateRequestDto.class), anyList())).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto))
                        .param("tempFilePaths", tempFilePaths))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(productService, times(1)).createProduct(any(ProductCreateRequestDto.class), anyList());
    }

    @Test
    @DisplayName("상품 단일 조회 테스트")
    public void getProductByIdTest() throws Exception {
        // Given
        Long productId = 1L;
        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(productId)
                .name("Test Product")
                .brand("Test Brand")
                .build();

        when(productService.getProductById(productId)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    public void deleteProductTest() throws Exception {
        // Given
        Long productId = 1L;
        when(productService.deleteProduct(any(ProductDeleteRequestDto.class))).thenReturn("Product deleted successfully.");

        // When & Then
        mockMvc.perform(delete("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully."));

        verify(productService, times(1)).deleteProduct(any(ProductDeleteRequestDto.class));
    }

    @Test
    @DisplayName("필터링된 상품 조회 테스트")
    public void getFilteredProductsTest() throws Exception {
        // Given
        ProductQueryDslRequestDto queryDto = ProductQueryDslRequestDto.builder()
                .mainCategoryId(1L)
                .subCategoryId(2L)
                .color("RED")
                .size("M")
                .build();

        Page<ProductQueryDslResponseDto> responsePage = mock(Page.class);
        when(productService.searchFilteredProducts(any(ProductQueryDslRequestDto.class), any())).thenReturn(responsePage);

        // When & Then
        mockMvc.perform(get("/api/products/filter")
                        .param("mainCategoryId", "1")
                        .param("subCategoryId", "2")
                        .param("color", "RED")
                        .param("size", "M"))
                .andExpect(status().isOk());

        verify(productService, times(1)).searchFilteredProducts(any(ProductQueryDslRequestDto.class), any());
    }
}
