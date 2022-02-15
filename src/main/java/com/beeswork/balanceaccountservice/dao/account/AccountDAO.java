package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;

import java.util.UUID;

public interface AccountDAO extends BaseDAO<Account> {

    Account findById(UUID accountId, boolean writeLock);
}
