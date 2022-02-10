package com.beeswork.balanceaccountservice.dao.wallet;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.Wallet;

import java.util.UUID;

public interface WalletDAO extends BaseDAO<Wallet> {
    Wallet findByAccountId(UUID accountId, boolean writeLock);
}
