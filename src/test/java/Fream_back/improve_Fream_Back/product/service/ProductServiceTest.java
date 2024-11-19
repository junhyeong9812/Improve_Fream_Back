package Fream_back.improve_Fream_Back.product.service;

import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.ProductImage;
import Fream_back.improve_Fream_Back.product.entity.enumType.ClothingSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.Color;
import Fream_back.improve_Fream_Back.product.entity.enumType.ShoeSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import Fream_back.improve_Fream_Back.product.entity.size.ProductSizeAndColorQuantity;
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
import java.util.Set;

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

    @Mock
    private ProductSizeAndColorQuantityRepository productSizeAndColorQuantityRepository;

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
                .sizeAndColorQuantities(Set.of(
                        ProductSizeAndColorQuantityDto.builder()
                                .colors(Set.of("RED", "BLUE"))
                                .clothingSizes(Set.of("M", "L"))
                                .quantity(10)
                                .build()
                ))
                .build();

        MainCategory mainCategory = MainCategory.builder().id(1L).name("Main Category").build();
        SubCategory subCategory = SubCategory.builder().id(2L).name("Sub Category").mainCategory(mainCategory).build();

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

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When: 상품 생성 메서드 호출
        ProductIdResponseDto response = productService.createProduct(productDto, List.of("tempFilePath"));

        // Then: 결과 검증
        assertNotNull(response);
        assertEquals(product.getId(), response.getId());

        verify(productRepository, times(1)).save(any(Product.class));
    }


    @Test
    @DisplayName("상품 수정 테스트")
    void updateProductTest() {
        // Given: 기존 상품과 카테고리 준비
        Long productId = 1L;
        MainCategory mainCategory = MainCategory.builder().id(1L).name("Main Category").build();
        SubCategory subCategory = SubCategory.builder().id(2L).name("Sub Category").mainCategory(mainCategory).build();

        // 기존 상품 생성
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

        // 기존 상품의 사이즈 및 색상 데이터 추가
        ProductSizeAndColorQuantity existingQuantity1 = ProductSizeAndColorQuantity.builder()
                .product(existingProduct)
                .sizeType(SizeType.SHOES)
                .shoeSize(ShoeSizeType.SIZE_260)
                .color(Color.RED)
                .quantity(10)
                .build();

        ProductSizeAndColorQuantity existingQuantity2 = ProductSizeAndColorQuantity.builder()
                .product(existingProduct)
                .sizeType(SizeType.SHOES)
                .shoeSize(ShoeSizeType.SIZE_270)
                .color(Color.BLUE)
                .quantity(5)
                .build();

        existingProduct.addSizeAndColorQuantity(existingQuantity1);
        existingProduct.addSizeAndColorQuantity(existingQuantity2);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(mainCategoryRepository.findById(1L)).thenReturn(Optional.of(mainCategory));
        when(subCategoryRepository.findById(2L)).thenReturn(Optional.of(subCategory));

        // Update 데이터 준비
        ProductUpdateRequestDto productDto = ProductUpdateRequestDto.builder()
                .name("Updated Product")
                .brand("Updated Brand")
                .sku("654321")
                .initialPrice(BigDecimal.valueOf(150.0))
                .description("Updated Description")
                .releaseDate(LocalDate.parse("2024-01-01"))
                .mainCategoryId(1L)
                .subCategoryId(2L)
                .sizeAndColorQuantities(Set.of(
                        ProductSizeAndColorQuantityDto.builder()
                                .colors(Set.of("GREEN"))
                                .shoeSizes(Set.of("SIZE_280", "SIZE_290"))
                                .quantity(15)
                                .build()
                ))
                .images(List.of()) // 이미지 초기화
                .build();

        // When: 상품 수정 메서드 호출
        ProductIdResponseDto response = productService.updateProduct(productId, productDto, List.of("tempFilePath"));

        // Then: 반환된 ID 확인 및 검증
        assertNotNull(response);
        assertEquals(productId, response.getId()); // 반환된 ID가 원래 ID와 동일한지 확인

        // 기존 데이터 삭제 검증 (기존 사이즈와 색상 데이터 삭제)
//        verify(productSizeAndColorQuantityRepository, times(2)).delete(any(ProductSizeAndColorQuantity.class));

        // 새 데이터 저장 검증 (새로운 사이즈와 색상 데이터 저장)
//        verify(productSizeAndColorQuantityRepository, times(2)).save(any(ProductSizeAndColorQuantity.class)); // GREEN x 2(280, 290) 저장
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

        ProductSizeAndColorQuantity quantity1 = ProductSizeAndColorQuantity.builder()
                .product(product)
                .sizeType(SizeType.CLOTHING)
                .clothingSize(ClothingSizeType.M)
                .color(Color.RED)
                .quantity(10)
                .build();

        ProductSizeAndColorQuantity quantity2 = ProductSizeAndColorQuantity.builder()
                .product(product)
                .sizeType(SizeType.SHOES)
                .shoeSize(ShoeSizeType.SIZE_140)
                .color(Color.BLUE)
                .quantity(5)
                .build();

        when(productRepository.findByIdWithDetails(productId)).thenReturn(product);
        when(productSizeAndColorQuantityRepository.findAllByProductId(productId)).thenReturn(List.of(quantity1, quantity2));

        // When: 상품 단일 조회 메서드 호출
        ProductResponseDto response = productService.getProductById(productId);

        // Then: 결과 검증
        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        assertEquals(2, response.getSizeAndColorQuantities().size()); // 두 개의 사이즈-색상 조합 검증
    }

    @Test
    @DisplayName("서브 카테고리를 포함한 필터링된 상품 조회 테스트")
    void searchFilteredProductsTest() {
        // Given: 필터링 조건과 페이지 정보 설정
        ProductQueryDslRequestDto queryDto = ProductQueryDslRequestDto.builder()
                .mainCategoryId(1L) // MainCategory ID
                .subCategoryId(2L)  // SubCategory ID
                .color("RED")
                .size("M")
                .build();

        Pageable pageable = Pageable.unpaged();

        // MainCategory 및 SubCategory 생성
        MainCategory mainCategory = MainCategory.builder().id(1L).name("Clothing").build();
        SubCategory subCategory1 = SubCategory.builder().id(2L).name("T-Shirts").mainCategory(mainCategory).build();

        // Product 생성
        Product product1 = Product.builder()
                .id(1L)
                .name("Red T-Shirt")
                .brand("Brand A")
                .sku("SKU001")
                .mainCategory(mainCategory)
                .subCategory(subCategory1)
                .initialPrice(BigDecimal.valueOf(100))
                .build();

        ProductSizeAndColorQuantity quantity1 = ProductSizeAndColorQuantity.builder()
                .product(product1)
                .sizeType(SizeType.CLOTHING)
                .clothingSize(ClothingSizeType.M)
                .color(Color.RED)
                .quantity(10)
                .build();

        product1.addSizeAndColorQuantity(quantity1);

        // Mock 데이터
        List<ProductQueryDslResponseDto> filteredProducts = List.of(
                ProductQueryDslResponseDto.builder()
                        .id(product1.getId())
                        .name(product1.getName())
                        .brand(product1.getBrand())
                        .mainCategoryId(mainCategory.getId())
                        .mainCategoryName(mainCategory.getName())
                        .subCategoryId(subCategory1.getId())
                        .subCategoryName(subCategory1.getName())
                        .colors(List.of("RED"))
                        .sizes(List.of("M"))
                        .quantity(10)
                        .build()
        );

        Page<ProductQueryDslResponseDto> mockPage = new PageImpl<>(filteredProducts, pageable, filteredProducts.size());

        // Repository Mock 설정
        when(productQueryRepository.findProductsByFilter(
                eq(1L), eq(2L), eq("RED"), eq("M"), anyString(), anyString(), eq(pageable)))
                .thenReturn(mockPage);

        // When: 필터링된 상품 조회 메서드를 호출
        Page<ProductQueryDslResponseDto> response = productService.searchFilteredProducts(queryDto, pageable);

        // Then: 결과 검증
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.getContent().size());
        assertEquals("Red T-Shirt", response.getContent().get(0).getName());
        assertEquals("Brand A", response.getContent().get(0).getBrand());
        assertEquals(1L, response.getContent().get(0).getMainCategoryId());
        assertEquals(2L, response.getContent().get(0).getSubCategoryId());
        assertEquals("RED", response.getContent().get(0).getColors().get(0));
        assertEquals("M", response.getContent().get(0).getSizes().get(0));
        assertEquals(10, response.getContent().get(0).getQuantity());

        // Repository 호출 검증
        verify(productQueryRepository, times(1)).findProductsByFilter(
                eq(1L), eq(2L), eq("RED"), eq("M"), anyString(), anyString(), eq(pageable));
    }

}
