package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.repository.SortOption;
import Fream_back.improve_Fream_Back.product.service.kafka.ViewEventProducer;
import Fream_back.improve_Fream_Back.product.service.product.ProductQueryService;
import Fream_back.improve_Fream_Back.user.entity.Gender;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductQueryService productQueryService;
    private final UserQueryService userQueryService; // 이메일 -> User 엔티티 조회
    private final ViewEventProducer viewEventProducer;

    @GetMapping
    public ResponseEntity<Page<ProductSearchResponseDto>> searchProducts(
            @ModelAttribute ProductSearchDto searchRequest,
            Pageable pageable) {
        // 유효성 검증
        searchRequest.validate();

        Page<ProductSearchResponseDto> response = productQueryService.searchProducts(
                searchRequest.getKeyword(),
                searchRequest.getCategoryIds(),
                searchRequest.getGenders(),
                searchRequest.getBrandIds(),
                searchRequest.getCollectionIds(),
                searchRequest.getColors(),
                searchRequest.getSizes(),
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                searchRequest.getSortOption(),
                pageable);
        return ResponseEntity.ok(response);
    }
    //뷰포인트가 없는 방식
    @GetMapping("/{productId}/detail")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(
            @PathVariable("productId") Long productId,
            @RequestParam("colorName") String colorName) {
        // 1) 상품 상세 (DB 조회)
        ProductDetailResponseDto detailDto = productQueryService.getProductDetail(productId, colorName);

        // 2) 이메일 추출 (익명 시 “anonymous”)
        String email = SecurityUtils.extractEmailOrAnonymous();

        // 3) 로그인 사용자라면 나이, 성별 조회
        Integer age = 0;
        Gender gender = Gender.OTHER;
        if (!"anonymous".equals(email)) {
            try {
                User user = userQueryService.findByEmail(email);
                age = (user.getAge() == null) ? 0 : user.getAge();
                gender = (user.getGender() == null) ? Gender.OTHER : user.getGender();
            } catch (IllegalArgumentException e) {
                // 만약 이메일이 있지만 User가 없다면 anonymous로 처리
                email = "anonymous";
            }
        }

        // 4) Producer에게 카프카 이벤트 발행
        viewEventProducer.sendViewEvent(detailDto.getColorId(), email, age, gender);

        // 5) 결과 반환
        return ResponseEntity.ok(detailDto);
    }


    @GetMapping("/{productId}/images")
    public ResponseEntity<byte[]> getProductImage(
            @PathVariable("productId") Long productId,
            @RequestParam("imageName") String imageName) throws Exception {
        // 파일 경로 설정
        String directory = "product/" + productId;
        File imageFile = new File(directory + File.separator + imageName);

        // 파일이 존재하지 않을 경우 예외 처리
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("이미지 파일이 존재하지 않습니다.");
        }

        // 파일 내용 읽기
        byte[] imageBytes = Files.readAllBytes(Paths.get(imageFile.getPath()));

        // 응답 생성 (Content-Type 설정)
        return ResponseEntity.ok()
                .header("Content-Type", Files.probeContentType(imageFile.toPath()))
                .body(imageBytes);
    }
}
