package Fream_back.improve_Fream_Back.address.service;

import Fream_back.improve_Fream_Back.address.dto.AddressResponseDto;
import Fream_back.improve_Fream_Back.address.entity.Address;
import Fream_back.improve_Fream_Back.address.repository.AddressRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressQueryService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    //주소지 목록 조회
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAddresses(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        return user.getAddresses().stream()
                .map(address -> new AddressResponseDto(
                        address.getId(),
                        address.getRecipientName(),
                        address.getPhoneNumber(),
                        address.getZipCode(),
                        address.getAddress(),
                        address.getDetailedAddress(),
                        address.isDefault()))
                .toList();
    }

    //단일 주소지 조회
    @Transactional(readOnly = true)
    public AddressResponseDto getAddress(String email, Long addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 주소록을 찾을 수 없습니다."));

        return new AddressResponseDto(
                address.getId(),
                address.getRecipientName(),
                address.getPhoneNumber(),
                address.getZipCode(),
                address.getAddress(),
                address.getDetailedAddress(),
                address.isDefault()
        );
    }
}
