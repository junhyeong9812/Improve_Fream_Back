package Fream_back.improve_Fream_Back.sale.service;

import Fream_back.improve_Fream_Back.sale.entity.Sale;
import Fream_back.improve_Fream_Back.sale.entity.SaleBankAccount;
import Fream_back.improve_Fream_Back.sale.repository.SaleBankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleBankAccountCommandService {

    private final SaleBankAccountRepository saleBankAccountRepository;

    @Transactional
    public SaleBankAccount createSaleBankAccount(String bankName, String accountNumber,
                                                 String accountHolder, Sale sale) {
        SaleBankAccount bankAccount = new SaleBankAccount(bankName, accountNumber, accountHolder, sale);
        return saleBankAccountRepository.save(bankAccount);
    }
}
