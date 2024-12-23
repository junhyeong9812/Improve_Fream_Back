package Fream_back.improve_Fream_Back.product.service.productSize;

import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.product.repository.ProductSizeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class ProductSizeQueryServiceTest {

    @Mock
    private ProductSizeRepository productSizeRepository;

    @InjectMocks
    private ProductSizeQueryService productSizeQueryService;

    ProductSizeQueryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ProductSize를 Color ID와 Size로 조회 - 성공")
    void findByColorIdAndSize_Success() {
        // Given
        Long colorId = 1L;
        String size = "M";
        ProductSize mockSize = ProductSize.builder()
                .id(1L)
                .size(size)
                .quantity(10)
                .build();
        given(productSizeRepository.findByProductColorIdAndSize(colorId, size)).willReturn(Optional.of(mockSize));

        // When
        Optional<ProductSize> result = productSizeQueryService.findByColorIdAndSize(colorId, size);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSize()).isEqualTo(size);
        assertThat(result.get().getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("ProductSize를 Color ID와 Size로 조회 - 실패")
    void findByColorIdAndSize_Failure() {
        // Given
        Long colorId = 1L;
        String size = "InvalidSize";
        given(productSizeRepository.findByProductColorIdAndSize(colorId, size)).willReturn(Optional.empty());

        // When
        Optional<ProductSize> result = productSizeQueryService.findByColorIdAndSize(colorId, size);

        // Then
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Color ID로 모든 사이즈 조회 - 성공")
    void findSizesByColorId_Success() {
        // Given
        Long colorId = 1L;
        List<ProductSize> mockSizes = List.of(
                ProductSize.builder().size("S").build(),
                ProductSize.builder().size("M").build(),
                ProductSize.builder().size("L").build()
        );
        given(productSizeRepository.findAllByProductColorId(colorId)).willReturn(mockSizes);

        // When
        List<String> result = productSizeQueryService.findSizesByColorId(colorId);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactlyInAnyOrder("S", "M", "L");
    }

    @Test
    @DisplayName("Color ID로 모든 사이즈 조회 - 실패")
    void findSizesByColorId_Failure() {
        // Given
        Long colorId = 1L;
        given(productSizeRepository.findAllByProductColorId(colorId)).willReturn(List.of());

        // When
        List<String> result = productSizeQueryService.findSizesByColorId(colorId);

        // Then
        assertThat(result).isEmpty();
    }
}
