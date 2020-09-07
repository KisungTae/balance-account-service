package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.AccountDTO;
import com.beeswork.balanceaccountservice.entity.Account;

public interface AccountDAO extends BaseDAO<Account> {

    void save(Account account);
    Account findByIdAndName(long id, String email);
}
