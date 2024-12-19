package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import Fream_back.improve_Fream_Back.user.entity.BankAccount;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestConfig.class, TestQueryDslConfig.class}) // QueryDSL Config도 가져오기
class BankAccountRepositoryTest {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private User user1; // TestConfig에서 제공되는 유저1

    @Test
    @DisplayName("은행 계좌 저장 및 조회 테스트")
    void testSaveAndFindBankAccount() {
        // Given
        BankAccount bankAccount = BankAccount.builder()
                .user(user1)
                .bankName("Test Bank")
                .accountNumber("123-456-789")
                .accountHolder("User1")
                .build();
        bankAccountRepository.save(bankAccount);

        // When
        BankAccount foundBankAccount = bankAccountRepository.findByUser_Email(user1.getEmail());

        // Then
        assertThat(foundBankAccount).isNotNull();
        assertThat(foundBankAccount.getBankName()).isEqualTo("Test Bank");
    }
    @Test
    @DisplayName("BankAccount 수정 테스트 (더티 체킹 확인)")
    @Transactional
    void testUpdateBankAccountWithDirtyChecking() {
        // Given
        BankAccount bankAccount = BankAccount.builder()
                .user(user1)
                .bankName("Old Bank")
                .accountNumber("123-456-789")
                .accountHolder("Old Holder")
                .build();
        bankAccountRepository.save(bankAccount);

        // When
        bankAccount.updateBankAccount("New Bank", "987-654-321", "New Holder");

        // Then
        BankAccount updatedBankAccount = bankAccountRepository.findById(bankAccount.getId()).orElseThrow();
        assertThat(updatedBankAccount.getBankName()).isEqualTo("New Bank");
        assertThat(updatedBankAccount.getAccountNumber()).isEqualTo("987-654-321");
        assertThat(updatedBankAccount.getAccountHolder()).isEqualTo("New Holder");
    }
}
