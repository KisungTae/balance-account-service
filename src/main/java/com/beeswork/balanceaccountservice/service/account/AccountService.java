package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.AccountDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;

public interface AccountService {

    void save(AccountDTO accountDTO) throws AccountNotFoundException;
}
