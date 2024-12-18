package Fream_back.improve_Fream_Back.user.service.bankaccount;

import Fream_back.improve_Fream_Back.user.dto.BankAccount.BankAccountDto;
import Fream_back.improve_Fream_Back.user.entity.BankAccount;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankAccountCommandService {
    private final UserRepository userRepository;

    @Transactional
    public void createOrUpdateBankAccount(String email, BankAccountDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getBankAccount() == null) {
            BankAccount bankAccount = BankAccount.builder()
                    .user(user)
                    .bankName(dto.getBankName())
                    .accountNumber(dto.getAccountNumber())
                    .accountHolder(dto.getAccountHolder())
                    .build();
            user.assignBankAccount(bankAccount);
        } else {
            user.getBankAccount().updateBankAccount(
                    dto.getBankName(),
                    dto.getAccountNumber(),
                    dto.getAccountHolder()
            );
        }
    }

    @Transactional
    public void deleteBankAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getBankAccount() != null) {
            user.removeBankAccount();
        } else {
            throw new IllegalArgumentException("삭제할 계좌가 존재하지 않습니다.");
        }
    }
}