package Fream_back.improve_Fream_Back.user.service.bankaccount;

import Fream_back.improve_Fream_Back.user.config.TestFollowConfig;
import Fream_back.improve_Fream_Back.user.dto.BankAccount.BankAccountDto;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(TestFollowConfig.class)
class BankAccountCommandServiceTest {

    @Autowired
    private BankAccountCommandService bankAccountCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private User user1;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("계좌 생성 - 성공")
    void testCreateBankAccountSuccess() {
        // Given
        String email = user1.getEmail();
        BankAccountDto dto = new BankAccountDto();
        dto.setBankName("Test Bank");
        dto.setAccountNumber("123-456-789");
        dto.setAccountHolder("User1");

        // When
        bankAccountCommandService.createOrUpdateBankAccount(email, dto);

        // Then
        User updatedUser = userRepository.findByEmail(email).orElseThrow();
        assertThat(updatedUser.getBankAccount()).isNotNull();
        assertThat(updatedUser.getBankAccount().getBankName()).isEqualTo(dto.getBankName());
        assertThat(updatedUser.getBankAccount().getAccountNumber()).isEqualTo(dto.getAccountNumber());
        assertThat(updatedUser.getBankAccount().getAccountHolder()).isEqualTo(dto.getAccountHolder());
    }

    @Test
    @DisplayName("계좌 업데이트 - 성공")
    void testUpdateBankAccountSuccess() {
        // Given
        String email = user1.getEmail();
        BankAccountDto initialDto = new BankAccountDto();
        initialDto.setBankName("Initial Bank");
        initialDto.setAccountNumber("111-222-333");
        initialDto.setAccountHolder("User1");

        bankAccountCommandService.createOrUpdateBankAccount(email, initialDto);

        BankAccountDto updatedDto = new BankAccountDto();
        updatedDto.setBankName("Updated Bank");
        updatedDto.setAccountNumber("444-555-666");
        updatedDto.setAccountHolder("Updated User1");

        // When
        bankAccountCommandService.createOrUpdateBankAccount(email, updatedDto);

        // Then
        User updatedUser = userRepository.findByEmail(email).orElseThrow();
        assertThat(updatedUser.getBankAccount()).isNotNull();
        assertThat(updatedUser.getBankAccount().getBankName()).isEqualTo(updatedDto.getBankName());
        assertThat(updatedUser.getBankAccount().getAccountNumber()).isEqualTo(updatedDto.getAccountNumber());
        assertThat(updatedUser.getBankAccount().getAccountHolder()).isEqualTo(updatedDto.getAccountHolder());
    }

    @Test
    @DisplayName("계좌 삭제 - 성공")
    void testDeleteBankAccountSuccess() {
        // Given
        String email = user1.getEmail();
        BankAccountDto dto = new BankAccountDto();
        dto.setBankName("Test Bank");
        dto.setAccountNumber("123-456-789");
        dto.setAccountHolder("User1");

        bankAccountCommandService.createOrUpdateBankAccount(email, dto);

        // 플러시 및 클리어
        entityManager.flush();
        entityManager.clear();

        // When
        bankAccountCommandService.deleteBankAccount(email);

        // Then
        User updatedUser = userRepository.findByEmail(email).orElseThrow();
        assertThat(updatedUser.getBankAccount()).isNull();
    }

    @Test
    @DisplayName("계좌 삭제 - 실패 (존재하지 않는 계좌)")
    void testDeleteBankAccountFailNoAccount() {
        // Given
        String email = user1.getEmail();

        // When & Then
        assertThatThrownBy(() -> bankAccountCommandService.deleteBankAccount(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제할 계좌가 존재하지 않습니다.");
    }
}
