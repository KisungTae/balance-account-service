package com.beeswork.balanceaccountservice.service.base;

import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import org.modelmapper.ModelMapper;

public abstract class BaseServiceImpl {

    protected final ModelMapper modelMapper;

    public BaseServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    protected void checkIfValid(Account account) {
        if (account == null) throw new AccountInvalidException();
    }
}
