package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;

import java.util.List;
import java.util.UUID;

public interface AccountInterService {

    void checkIfValid(UUID accountId, String email);
    void checkIfValid(Account account, String email);
    void checkIfBlocked(Account account);
    Account findValid(UUID accountId, String email);
    List<Object[]> findAllWithin(UUID accountId, String email, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude);

    void persist(Account account);
}
