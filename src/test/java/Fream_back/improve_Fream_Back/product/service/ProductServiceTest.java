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
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    @DisplayName("상품 생성 및 임시 파일 이동 테스트")
    void createProductTest() throws Exception {
        // Given: 임시 파일 저장
        String tempFilePath1 = fileStorageUtil.saveTemporaryFile(new MockMultipartFile(
                "file", "image1.jpg", "image/jpeg", "dummy content".getBytes()));
        String tempFilePath2 = fileStorageUtil.saveTemporaryFile(new MockMultipartFile(
                "file", "image2.jpg", "image/jpeg", "dummy content".getBytes()));

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
                .images(List.of(
                        ProductImageDto.builder()
                                .imageName("image1.jpg")
                                .temp_Url(tempFilePath1)
                                .imageType("thumbnail")
                                .isMainThumbnail(true)
                                .build(),
                        ProductImageDto.builder()
                                .imageName("image2.jpg")
                                .temp_Url(tempFilePath2)
                                .imageType("detail")
                                .isMainThumbnail(false)
                                .build()
                ))
                .build();

        MainCategory mainCategory = MainCategory.builder().id(1L).name("Main Category").build();
        SubCategory subCategory = SubCategory.builder().id(2L).name("Sub Category").mainCategory(mainCategory).build();

        when(mainCategoryRepository.findById(1L)).thenReturn(Optional.of(mainCategory));
        when(subCategoryRepository.findById(2L)).thenReturn(Optional.of(subCategory));

        // When
        ProductIdResponseDto response = productService.createProduct(productDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId()); // ID 검증

        // 파일 이동 검증
        assertTrue(Files.exists(Paths.get("images/product_1_image1.jpg")));
        assertTrue(Files.exists(Paths.get("images/product_1_image2.jpg")));

        // 임시 파일 삭제 검증
        assertFalse(Files.exists(Paths.get(tempFilePath1)));
        assertFalse(Files.exists(Paths.get(tempFilePath2)));

        // 테스트 종료 후 생성된 파일 삭제
        Files.deleteIfExists(Paths.get("images/product_1_image1.jpg"));
        Files.deleteIfExists(Paths.get("images/product_1_image2.jpg"));
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

        // 업데이트 후 사이즈 및 색상 데이터 검증
        Set<ProductSizeAndColorQuantity> updatedQuantities = existingProduct.getSizeAndColorQuantities();
        assertEquals(4, updatedQuantities.size()); // 새 데이터 2개 추가 확인

        // 예상 데이터 확인
        assertTrue(updatedQuantities.stream().anyMatch(quantity ->
                quantity.getShoeSize() == ShoeSizeType.SIZE_280 &&
                        quantity.getColor() == Color.GREEN &&
                        quantity.getQuantity() == 15));

        assertTrue(updatedQuantities.stream().anyMatch(quantity ->
                quantity.getShoeSize() == ShoeSizeType.SIZE_290 &&
                        quantity.getColor() == Color.GREEN &&
                        quantity.getQuantity() == 15));
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
        // Given: 생성된 상품 데이터 준비
        Long productId = 1L;
        MainCategory mainCategory = MainCategory.builder().id(1L).name("Main Category").build();
        SubCategory subCategory = SubCategory.builder().id(2L).name("Sub Category").mainCategory(mainCategory).build();

        // 상품 생성
        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .brand("Brand A")
                .sku("SKU001")
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .initialPrice(BigDecimal.valueOf(100))
                .description("Description")
                .releaseDate(LocalDate.parse("2023-12-31"))
                .build();

        // 사이즈와 색상 데이터 추가
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

        product.addSizeAndColorQuantity(quantity1);
        product.addSizeAndColorQuantity(quantity2);

        // Mock 설정
        when(productRepository.findByIdWithDetails(productId)).thenReturn(product);

        // When: 단일 조회 호출
        ProductResponseDto response = productService.getProductById(productId);

        // Then: 결과 검증
        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        assertEquals(2, response.getSizeAndColorQuantities().size());

        // 사이즈 및 색상 데이터 확인
        assertTrue(response.getSizeAndColorQuantities().stream().anyMatch(quantity ->
                quantity.getClothingSizes().contains("M") &&
                        quantity.getColors().contains("RED") &&
                        quantity.getQuantity() == 10));

        assertTrue(response.getSizeAndColorQuantities().stream().anyMatch(quantity ->
                quantity.getShoeSizes().contains("SIZE_140") &&
                        quantity.getColors().contains("BLUE") &&
                        quantity.getQuantity() == 5));
    }

    @Test
    @DisplayName("서브 카테고리를 포함한 필터링된 상품 조회 테스트")
    void searchFilteredProductsTest() {
        // Given: 생성된 상품 데이터와 필터 조건 준비
        ProductQueryDslRequestDto queryDto = ProductQueryDslRequestDto.builder()
                .mainCategoryId(1L)
                .subCategoryId(2L)
                .color("RED")
                .size("M")
                .build();

        Pageable pageable = Pageable.unpaged();

        // 카테고리 생성
        MainCategory mainCategory = MainCategory.builder().id(1L).name("Clothing").build();
        SubCategory subCategory = SubCategory.builder().id(2L).name("T-Shirts").mainCategory(mainCategory).build();

        // 상품 생성
        Product product = Product.builder()
                .id(1L)
                .name("Red T-Shirt")
                .brand("Brand A")
                .sku("SKU001")
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .initialPrice(BigDecimal.valueOf(100))
                .description("Description")
                .releaseDate(LocalDate.parse("2023-12-31"))
                .build();

        // 사이즈와 색상 데이터 추가
        ProductSizeAndColorQuantity quantity = ProductSizeAndColorQuantity.builder()
                .product(product)
                .sizeType(SizeType.CLOTHING)
                .clothingSize(ClothingSizeType.M)
                .color(Color.RED)
                .quantity(10)
                .build();

        product.addSizeAndColorQuantity(quantity);

        // Mock 데이터
        List<ProductQueryDslResponseDto> filteredProducts = List.of(
                ProductQueryDslResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .brand(product.getBrand())
                        .mainCategoryId(mainCategory.getId())
                        .mainCategoryName(mainCategory.getName())
                        .subCategoryId(subCategory.getId())
                        .subCategoryName(subCategory.getName())
                        .colors(List.of("RED"))
                        .sizes(List.of("M"))
                        .quantity(10)
                        .build()
        );

        Page<ProductQueryDslResponseDto> mockPage = new PageImpl<>(filteredProducts, pageable, filteredProducts.size());

        // Mock 설정
        when(productQueryRepository.findProductsByFilter(
                eq(1L), eq(2L), eq("RED"), eq("M"), isNull(), isNull(), eq(pageable))
        ).thenReturn(mockPage);

        // When: 필터링된 상품 조회 호출
        Page<ProductQueryDslResponseDto> response = productService.searchFilteredProducts(queryDto, pageable);

        // Then: 결과 검증
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.getContent().size());

        verify(productQueryRepository, times(1)).findProductsByFilter(
                eq(1L), eq(2L), eq("RED"), eq("M"), isNull(), isNull(), eq(pageable));

        // 필터링된 데이터 확인
        ProductQueryDslResponseDto result = response.getContent().get(0);
        assertEquals("Red T-Shirt", result.getName());
        assertEquals("Brand A", result.getBrand());
        assertEquals(1L, result.getMainCategoryId());
        assertEquals(2L, result.getSubCategoryId());
        assertEquals("RED", result.getColors().get(0));
        assertEquals("M", result.getSizes().get(0));
        assertEquals(10, result.getQuantity());
    }

}
