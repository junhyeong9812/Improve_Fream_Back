package Fream_back.improve_Fream_Back.product.service;

import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.ProductImage;
import Fream_back.improve_Fream_Back.product.repository.*;
import Fream_back.improve_Fream_Back.Category.repository.MainCategoryRepository;
import Fream_back.improve_Fream_Back.Category.repository.SubCategoryRepository;
import Fream_back.improve_Fream_Back.product.service.fileStorageUtil.FileStorageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MainCategoryRepository mainCategoryRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductQueryRepository productQueryRepository;

    @Mock
    private FileStorageUtil fileStorageUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품 생성 테스트")
    void createProductTest() {
        // Given: 상품 생성에 필요한 데이터 준비
        ProductCreateRequestDto productDto = ProductCreateRequestDto.builder()
                .name("Product Name")
                .brand("Brand")
                .sku("123456")
                .initialPrice(BigDecimal.valueOf(100.0))
                .description("Product Description")
                .releaseDate(LocalDate.parse("2023-12-31"))
                .mainCategoryId(1L)
                .subCategoryId(2L)
                .build();

        MainCategory mainCategory = MainCategory.builder().name("Main Category").build();
        SubCategory subCategory = SubCategory.builder().name("Sub Category").mainCategory(mainCategory).build();

        when(mainCategoryRepository.findById(1L)).thenReturn(Optional.of(mainCategory));
        when(subCategoryRepository.findById(2L)).thenReturn(Optional.of(subCategory));

        Product product = Product.builder()
                .name("Product Name")
                .brand("Brand")
                .sku("123456")
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .initialPrice(BigDecimal.valueOf(100.0))
                .description("Product Description")
                .releaseDate(LocalDate.parse("2023-12-31"))
                .build();

        Product savedProduct = Product.builder()
                .name("Product Name")
                .brand("Brand")
                .sku("123456")
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .initialPrice(BigDecimal.valueOf(100.0))
                .description("Product Description")
                .releaseDate(LocalDate.parse("2023-12-31"))
                .build();

        when(productRepository.save(product)).thenReturn(savedProduct);

        // When: 상품 생성 메서드 호출
        ProductIdResponseDto response = productService.createProduct(productDto, List.of("tempFilePath"));

        // Then: 결과 검증
        assertNotNull(response);
        verify(productRepository, times(1)).save(product);
    }


    @Test
    @DisplayName("상품 수정 테스트")
    void updateProductTest() {
        // Given: 기존 상품과 카테고리 준비
        Long productId = 1L;
        MainCategory mainCategory = MainCategory.builder().id(1L).name("Main Category").build();
        SubCategory subCategory = SubCategory.builder().id(2L).name("Sub Category").mainCategory(mainCategory).build();

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Original Product")
                .brand("Original Brand")
                .sku("123456")
                .initialPrice(BigDecimal.valueOf(100.0))
                .description("Original Description")
                .releaseDate(LocalDate.parse("2023-12-31"))
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(mainCategoryRepository.findById(1L)).thenReturn(Optional.of(mainCategory));
        when(subCategoryRepository.findById(2L)).thenReturn(Optional.of(subCategory));

        ProductUpdateRequestDto productDto = ProductUpdateRequestDto.builder()
                .name("Updated Product")
                .brand("Updated Brand")
                .sku("654321")
                .initialPrice(BigDecimal.valueOf(150.0))
                .description("Updated Description")
                .releaseDate(LocalDate.parse("2024-01-01"))
                .mainCategoryId(1L)
                .subCategoryId(2L)
                .images(List.of()) // 이미지 초기화
                .build();

        // When: 상품 수정 메서드 호출
        ProductIdResponseDto response = productService.updateProduct(productId, productDto, List.of("tempFilePath"));

        // Then: 반환된 ID 확인 및 검증
        assertNotNull(response);
        assertEquals(productId, response.getId()); // 반환된 ID가 원래 ID와 동일한지 확인

        // 저장 호출 검증: 더티 체킹을 사용하므로 save()는 호출되지 않음
        verify(productRepository, never()).save(any(Product.class));

        // 엔티티의 필드 값이 올바르게 업데이트되었는지 검증
        assertEquals("Updated Product", existingProduct.getName());
        assertEquals("Updated Brand", existingProduct.getBrand());
        assertEquals("654321", existingProduct.getSku());
        assertEquals(BigDecimal.valueOf(150.0), existingProduct.getInitialPrice());
        assertEquals("Updated Description", existingProduct.getDescription());
        assertEquals(LocalDate.parse("2024-01-01"), existingProduct.getReleaseDate());
    }




    @Test
    @DisplayName("상품 삭제 테스트")
    void deleteProductTest() {
        // Given: 삭제할 상품과 관련 데이터를 준비
        Long productId = 1L;
        Product product = Product.builder().id(productId).build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When: 상품 삭제 메서드를 호출
        String result = productService.deleteProduct(ProductDeleteRequestDto.builder().id(productId).build());

        // Then: 결과 검증
        assertEquals("Product deleted successfully.", result);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("상품 단일 조회 테스트")
    void getProductByIdTest() {
        // Given: 조회할 상품 데이터 준비
        Long productId = 1L;
        MainCategory mainCategory = MainCategory.builder().id(1L).name("Main Category").build();
        SubCategory subCategory = SubCategory.builder().id(2L).name("Sub Category").mainCategory(mainCategory).build();

        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .mainCategory(mainCategory)  // mainCategory 설정
                .subCategory(subCategory)    // subCategory 설정
                .build();
        when(productRepository.findByIdWithDetails(productId)).thenReturn(product);

        // When: 상품 단일 조회 메서드를 호출
        ProductResponseDto response = productService.getProductById(productId);

        // Then: 결과 검증
        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        verify(productRepository, times(1)).findByIdWithDetails(productId);
    }

    @Test
    @DisplayName("서브 카테고리를 포함한 필터링된 상품 조회 테스트")
    void searchFilteredProductsTest() {
        // Given: 필터링 조건과 페이지 정보 설정
        ProductQueryDslRequestDto queryDto = ProductQueryDslRequestDto.builder()
                .mainCategoryId(1L) // MainCategory ID
                .subCategoryId(2L)  // SubCategory ID
                .color("Red")
                .size("L")
                .build();

        Pageable pageable = Pageable.unpaged();

        // 카테고리 데이터 생성
        MainCategory mainCategory = MainCategory.builder()
                .id(1L)
                .name("Clothing")
                .build();

        SubCategory subCategory1 = SubCategory.builder()
                .id(2L)
                .name("T-Shirts")
                .mainCategory(mainCategory)
                .build();

        SubCategory subCategory2 = SubCategory.builder()
                .id(3L)
                .name("Jackets")
                .mainCategory(mainCategory)
                .build();

        // 상품 데이터 생성 (3개의 상품)
        ProductQueryDslResponseDto product1 = ProductQueryDslResponseDto.builder()
                .id(1L)
                .name("Red T-Shirt L")
                .brand("Brand A")
                .color("Red")
                .size("L")
                .mainCategoryId(1L) // MainCategory와 SubCategory1
                .subCategoryId(2L)
                .build();

        ProductQueryDslResponseDto product2 = ProductQueryDslResponseDto.builder()
                .id(2L)
                .name("Blue T-Shirt M")
                .brand("Brand B")
                .color("Blue")
                .size("M")
                .mainCategoryId(1L) // MainCategory와 SubCategory1
                .subCategoryId(2L)
                .build();

        ProductQueryDslResponseDto product3 = ProductQueryDslResponseDto.builder()
                .id(3L)
                .name("Green Jacket S")
                .brand("Brand C")
                .color("Green")
                .size("S")
                .mainCategoryId(1L) // MainCategory와 SubCategory2
                .subCategoryId(3L)
                .build();

        // Mock 데이터 리스트 생성 (SubCategoryId 2L에 해당하는 데이터만 포함)
        List<ProductQueryDslResponseDto> filteredProducts = List.of(product1, product2);
        Page<ProductQueryDslResponseDto> mockPage = new PageImpl<>(filteredProducts, pageable, filteredProducts.size());

        // Repository Mock 설정
        when(productQueryRepository.findProductsByFilter(
                eq(1L), eq(2L), eq("Red"), eq("L"), anyString(), anyString(), eq(pageable)))
                .thenReturn(mockPage);

        // When: 필터링된 상품 조회 메서드를 호출
        Page<ProductQueryDslResponseDto> response = productService.searchFilteredProducts(queryDto, pageable);

        // Then: 결과 검증
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(2, response.getContent().size()); // 필터링된 상품은 2개
        assertEquals("Red T-Shirt L", response.getContent().get(0).getName()); // 첫 번째 상품 이름 검증
        assertEquals("Blue T-Shirt M", response.getContent().get(1).getName()); // 두 번째 상품 이름 검증

        // 카테고리 필터 조건 검증
        assertEquals(1L, response.getContent().get(0).getMainCategoryId()); // MainCategory 검증
        assertEquals(2L, response.getContent().get(0).getSubCategoryId()); // SubCategory 검증
        assertEquals(2L, response.getContent().get(1).getSubCategoryId()); // SubCategory 검증

        // 다른 카테고리 상품이 필터링되지 않았는지 검증
        assertNotEquals(3L, response.getContent().get(0).getSubCategoryId()); // SubCategory2 제외

        // Repository 호출 검증
        verify(productQueryRepository, times(1)).findProductsByFilter(
                eq(1L), eq(2L), eq("Red"), eq("L"), anyString(), anyString(), eq(pageable));
    }

}
