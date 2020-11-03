package com.beeswork.balanceaccountservice.service.base;

import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountEmailNotMatchException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import org.modelmapper.ModelMapper;

public abstract class BaseServiceImpl {

    protected final ModelMapper modelMapper;

    public BaseServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    protected void checkIfValid(Account account, String email) {
        checkIfValid(account);
        if (email == null) throw new AccountNotFoundException();
        if (!account.getEmail().equals(email)) throw new AccountNotFoundException();
    }

    protected void checkIfValid(Account account) {
        if (account == null) throw new AccountNotFoundException();
        if (account.isBlocked()) throw new AccountBlockedException();
    }
}
