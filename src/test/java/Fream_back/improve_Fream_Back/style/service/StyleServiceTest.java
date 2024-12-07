package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.repository.OrderItemRepository;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.style.dto.StyleResponseDto;
import Fream_back.improve_Fream_Back.style.dto.StyleSearchDto;
import Fream_back.improve_Fream_Back.style.dto.StyleUpdateDto;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.repository.StyleRepository;
import Fream_back.improve_Fream_Back.style.service.styleFileUtil.StyleFileStorageUtil;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@SpringBootTest
//@Transactional
class StyleServiceTest {

    @InjectMocks
    private StyleService styleService;

    @Mock
    private StyleFileStorageUtil fileStorageUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private StyleRepository styleRepository;

    private User testUser;
    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 사용자 데이터 생성
        testUser = User.builder()
                .id(1L) // ID 추가
                .loginId("testLogin")
                .password("encodedPassword")
                .nickname("TestUser")
                .realName("Test Real Name")
                .phoneNumber("010-1234-5678")
                .email("testuser@example.com")
                .phoneNotificationConsent(true)
                .emailNotificationConsent(false)
                .build();

        testOrder = Order.builder()
                .id(1L) // ID 추가
                .user(testUser)
                .recipientName("Recipient Name")
                .phoneNumber("010-1234-5678")
                .address("123 Test Street")
                .addressDetail("Apt 101")
                .zipCode("12345")
                .paymentCompleted(true)
                .totalPrice(BigDecimal.valueOf(100.0))
                .build();

        testOrderItem = OrderItem.builder()
                .id(1L) // ID 추가
                .order(testOrder)
                .product(null) // Product 추가 필요
                .quantity(1)
                .price(BigDecimal.valueOf(50.0))
                .build();

        // Mock 설정
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(orderItemRepository.findById(testOrderItem.getId())).thenReturn(Optional.of(testOrderItem));
        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
    }

    @Test
    @DisplayName("스타일 생성 - 성공적으로 스타일 ID 반환")
    void createStyle_ShouldReturnStyleId() throws Exception {
        // Given
        String content = "Test Content";
        Integer rating = 5;
        String tempFilePath = "temp/path/file.jpg";
        String finalFilePath = "final/path/file.jpg";

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("file.jpg");
        when(fileStorageUtil.saveTemporaryFile(mockFile)).thenReturn(tempFilePath);
        when(fileStorageUtil.moveToPermanentStorage(tempFilePath, testOrderItem.getId())).thenReturn(finalFilePath);

        // When
        Long styleId = styleService.createStyle(testUser.getId(), testOrderItem.getId(), content, rating, mockFile);

        // Then
        assertNotNull(styleId);
        Style createdStyle = styleRepository.findById(styleId).orElse(null);
        assertNotNull(createdStyle);
        assertEquals(content, createdStyle.getContent());
        assertEquals(rating, createdStyle.getRating());
    }

    @Test
    @DisplayName("스타일 수정 - 성공적으로 수정된 스타일 ID 반환")
    void updateStyle_ShouldReturnUpdatedStyleId() throws Exception {
        // Given
        Style style = Style.builder()
                .id(1L)
                .user(testUser)
                .orderItem(testOrderItem)
                .content("Old Content")
                .rating(3)
                .build();

        // Mock 설정
        when(styleRepository.findById(style.getId())).thenReturn(Optional.of(style));
        String tempFilePath = "temp/path/updated-file.jpg";
        String finalFilePath = "final/path/updated-file.jpg";
        StyleUpdateDto updateDto = new StyleUpdateDto(testUser.getId(), "Updated Content", 4, tempFilePath);

        when(fileStorageUtil.moveToPermanentStorage(tempFilePath, style.getId())).thenReturn(finalFilePath);

        // When
        Long updatedStyleId = styleService.updateStyle(style.getId(), updateDto);

        // Then
        assertNotNull(updatedStyleId);
        Style updatedStyle = styleRepository.findById(updatedStyleId).orElse(null);
        assertNotNull(updatedStyle);
        assertEquals("Updated Content", updatedStyle.getContent());
        assertEquals(4, updatedStyle.getRating());
    }

    @Test
    @DisplayName("스타일 삭제 - 성공적으로 삭제")
    void deleteStyle_ShouldDeleteStyle() throws Exception {
        Style style = Style.builder()
                .id(1L)
                .user(testUser)
                .orderItem(testOrderItem)
                .content("To be deleted")
                .imageUrl("old/path/image.jpg")
                .build();

        when(styleRepository.findById(style.getId())).thenReturn(Optional.of(style));

        styleService.deleteStyle(style.getId(), testUser.getId());

        verify(styleRepository).delete(style);
    }

    @Test
    @DisplayName("스타일 상세 조회 - 실제 데이터 기반")
    void getStyleById_ShouldReturnStyleResponseDto() {
        // Given
        Style style = Style.builder()
                .user(testUser)
                .orderItem(testOrderItem)
                .content("Test Content")
                .rating(5)
                .build();
        styleRepository.save(style);

        // When
        StyleResponseDto result = styleService.getStyleById(style.getId());

        // Then
        assertNotNull(result);
        assertEquals(style.getId(), result.getId());
        assertEquals(style.getContent(), result.getContent());
        assertEquals(testUser.getNickname(), result.getUserNickname());
    }

    @Test
    @DisplayName("스타일 목록 조회 - 모의 페이징 데이터 사용")
    void getPagedStyles_ShouldReturnPagedStyles() {
        // Given
        StyleSearchDto searchDto = new StyleSearchDto(testUser.getId(), null, "Test");
        PageRequest pageable = PageRequest.of(0, 20);

        // Mock 데이터 생성
        StyleResponseDto mockResponse = new StyleResponseDto(
                1L, "Test Content", 5, "image.jpg", null,
                LocalDateTime.now(), "TestUser", 1L, "Test Product", "Test Brand", "thumb.jpg"
        );
        Page<StyleResponseDto> mockPage = new PageImpl<>(List.of(mockResponse));

        when(styleRepository.searchStyles(searchDto, pageable)).thenReturn(mockPage);

        // When
        Page<StyleResponseDto> result = styleService.getPagedStyles(searchDto, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Content", result.getContent().get(0).getContent());
    }
}