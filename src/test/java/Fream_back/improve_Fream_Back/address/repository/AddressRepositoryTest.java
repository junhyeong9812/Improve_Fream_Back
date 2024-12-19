package Fream_back.improve_Fream_Back.address.repository;

import Fream_back.improve_Fream_Back.address.entity.Address;
import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestConfig.class, TestQueryDslConfig.class}) // QueryDSL Config도 가져오기
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private User user1; // TestConfig에서 제공되는 유저1

    @Test
    @DisplayName("Address 저장 및 조회 테스트")
    void testSaveAndFindAddress() {
        // Given
        Address address = Address.builder()
                .user(user1)
                .recipientName("John Doe")
                .phoneNumber("010-1234-5678")
                .zipCode("12345")
                .address("Seoul, Korea")
                .detailedAddress("123-45")
                .isDefault(true)
                .build();
        addressRepository.save(address);

        // When
        Optional<Address> foundAddress = addressRepository.findById(address.getId());

        // Then
        assertThat(foundAddress).isPresent();
        assertThat(foundAddress.get().getRecipientName()).isEqualTo("John Doe");
        assertThat(foundAddress.get().isDefault()).isTrue();
    }

    @Test
    @DisplayName("특정 사용자와 Address ID로 조회 테스트")
    void testFindByIdAndUser() {
        // Given
        Address address = Address.builder()
                .user(user1)
                .recipientName("Jane Doe")
                .phoneNumber("010-9876-5432")
                .zipCode("54321")
                .address("Busan, Korea")
                .detailedAddress("456-78")
                .isDefault(false)
                .build();
        addressRepository.save(address);

        // When
        Optional<Address> foundAddress = addressRepository.findByIdAndUser(address.getId(), user1);

        // Then
        assertThat(foundAddress).isPresent();
        assertThat(foundAddress.get().getRecipientName()).isEqualTo("Jane Doe");
        assertThat(foundAddress.get().getAddress()).isEqualTo("Busan, Korea");
    }

    @Test
    @DisplayName("Address 삭제 테스트")
    void testDeleteAddress() {
        // Given
        Address address = Address.builder()
                .user(user1)
                .recipientName("John Smith")
                .phoneNumber("010-1111-2222")
                .zipCode("67890")
                .address("Incheon, Korea")
                .detailedAddress("789-10")
                .isDefault(false)
                .build();
        addressRepository.save(address);
        Long addressId = address.getId();

        // When
        addressRepository.deleteById(addressId);

        // Then
        Optional<Address> deletedAddress = addressRepository.findById(addressId);
        assertThat(deletedAddress).isNotPresent();
    }

    @Test
    @DisplayName("Address 수정 테스트 (더티 체킹 확인)")
    @Transactional
    void testUpdateAddressWithDirtyChecking() {
        // Given
        Address address = Address.builder()
                .user(user1)
                .recipientName("Old Name")
                .phoneNumber("010-2222-3333")
                .zipCode("22222")
                .address("Old Address")
                .detailedAddress("Old Detailed Address")
                .isDefault(false)
                .build();
        addressRepository.save(address);

        // When
        address.updateAddress("New Name", "010-3333-4444", "33333", "New Address", "New Detailed Address", true);

        // Then
        Address updatedAddress = addressRepository.findById(address.getId()).orElseThrow();
        assertThat(updatedAddress.getRecipientName()).isEqualTo("New Name");
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo("010-3333-4444");
        assertThat(updatedAddress.getZipCode()).isEqualTo("33333");
        assertThat(updatedAddress.getAddress()).isEqualTo("New Address");
        assertThat(updatedAddress.getDetailedAddress()).isEqualTo("New Detailed Address");
        assertThat(updatedAddress.isDefault()).isTrue();
    }
}
