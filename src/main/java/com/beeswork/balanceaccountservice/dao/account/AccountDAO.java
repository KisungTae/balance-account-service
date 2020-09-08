package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;

import java.util.UUID;

public interface AccountDAO extends BaseDAO<Account> {

    Account findById(UUID id) throws AccountNotFoundException;
//    Account findByIdAndName(long id, String email);
}
