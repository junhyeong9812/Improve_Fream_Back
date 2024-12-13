package Fream_back.improve_Fream_Back.address.controller;

import Fream_back.improve_Fream_Back.address.dto.AddressCreateDto;
import Fream_back.improve_Fream_Back.address.dto.AddressListResponseDto;
import Fream_back.improve_Fream_Back.address.dto.AddressResponseDto;
import Fream_back.improve_Fream_Back.address.dto.AddressUpdateDto;
import Fream_back.improve_Fream_Back.address.service.AddressCommandService;
import Fream_back.improve_Fream_Back.address.service.AddressQueryService;
import Fream_back.improve_Fream_Back.user.Jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressCommandService addressCommandService;
    private final AddressQueryService addressQueryService;
    // SecurityContextHolder에서 이메일 추출
    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다."); // 인증 실패 처리
    }

    @PostMapping
    public ResponseEntity<String> createAddress(@RequestBody @Validated AddressCreateDto createDto) {
        String email = extractEmailFromSecurityContext();
        addressCommandService.createAddress(email, createDto);
        return ResponseEntity.ok("주소록 생성이 완료되었습니다.");
    }

    @PutMapping
    public ResponseEntity<String> updateAddress(@RequestBody @Validated AddressUpdateDto updateDto) {
        String email = extractEmailFromSecurityContext();
        addressCommandService.updateAddress(email, updateDto);
        return ResponseEntity.ok("주소록 수정이 완료되었습니다.");
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        String email = extractEmailFromSecurityContext();
        addressCommandService.deleteAddress(email, addressId);
        return ResponseEntity.ok("주소록 삭제가 완료되었습니다.");
    }

    @GetMapping
    public ResponseEntity<AddressListResponseDto> getAddresses() {
        String email = extractEmailFromSecurityContext();
        List<AddressResponseDto> addresses = addressQueryService.getAddresses(email);
        return ResponseEntity.ok(new AddressListResponseDto(addresses));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(@PathVariable Long addressId) {
        String email = extractEmailFromSecurityContext();
        AddressResponseDto address = addressQueryService.getAddress(email, addressId);
        return ResponseEntity.ok(address);
    }
}
