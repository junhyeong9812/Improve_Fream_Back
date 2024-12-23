package Fream_back.improve_Fream_Back.product.service.productColor;

import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.repository.ProductColorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class ProductColorQueryServiceTest {

    @Mock
    private ProductColorRepository productColorRepository;

    @InjectMocks
    private ProductColorQueryService productColorQueryService;

    ProductColorQueryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ProductColor를 ID로 조회 - 성공")
    void findById_Success() {
        // Given
        Long colorId = 1L;
        ProductColor mockColor = ProductColor.builder()
                .id(colorId)
                .colorName("Red")
                .build();
        given(productColorRepository.findById(colorId)).willReturn(Optional.of(mockColor));

        // When
        ProductColor result = productColorQueryService.findById(colorId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(colorId);
        assertThat(result.getColorName()).isEqualTo("Red");
    }

    @Test
    @DisplayName("ProductColor를 ID로 조회 - 실패")
    void findById_Failure() {
        // Given
        Long invalidColorId = -1L;
        given(productColorRepository.findById(invalidColorId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productColorQueryService.findById(invalidColorId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 ProductColor를 찾을 수 없습니다");
    }
}
