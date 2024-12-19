package Fream_back.improve_Fream_Back.user.service.bankaccount;

import Fream_back.improve_Fream_Back.user.config.TestFollowConfig;
import Fream_back.improve_Fream_Back.user.dto.BankAccount.BankAccountDto;
import Fream_back.improve_Fream_Back.user.dto.BankAccount.BankAccountInfoDto;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class BankAccountQueryServiceTest {

    @Autowired
    private BankAccountCommandService bankAccountCommandService;

    @Autowired
    private BankAccountQueryService bankAccountQueryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private User user1;

    @Autowired
    private User user2;

    @BeforeEach
    void setup() {
        // BankAccount 등록
        BankAccountDto dto = new BankAccountDto();
        dto.setBankName("Test Bank");
        dto.setAccountNumber("123-456-789");
        dto.setAccountHolder("User1");

        bankAccountCommandService.createOrUpdateBankAccount(user1.getEmail(), dto);
        // 플러시를 통해 DB에 반영 후 영속성 컨텍스트 초기화
        userRepository.flush();
    }

    @Test
    @DisplayName("은행 계좌 조회 - 성공")
    void testGetBankAccountSuccess() {
        // When
        BankAccountInfoDto bankAccountInfo = bankAccountQueryService.getBankAccount(user1.getEmail());

        // Then
        assertThat(bankAccountInfo).isNotNull();
        assertThat(bankAccountInfo.getBankName()).isEqualTo("Test Bank");
        assertThat(bankAccountInfo.getAccountNumber()).isEqualTo("123-456-789");
        assertThat(bankAccountInfo.getAccountHolder()).isEqualTo("User1");
    }

    @Test
    @DisplayName("은행 계좌 조회 - 실패 (등록된 계좌 없음)")
    void testGetBankAccountFailNoAccount() {
        // Given
        String emailWithoutAccount = user2.getEmail(); // TestFollowConfig에서 제공되는 user2 활용

        // When & Then
        assertThatThrownBy(() -> bankAccountQueryService.getBankAccount(emailWithoutAccount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록된 계좌가 없습니다.");
    }

}
