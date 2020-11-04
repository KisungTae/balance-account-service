package com.beeswork.balanceaccountservice.service.base;

import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedBlockedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import org.modelmapper.ModelMapper;

public abstract class BaseServiceImpl {

    protected final ModelMapper modelMapper;

    public BaseServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    protected void checkIfAccountValid(Account account, String email) {
        checkIfAccountValid(account);
        if (!account.getEmail().equals(email)) throw new AccountNotFoundException();
    }

    protected void checkIfAccountValid(Account account) {
        if (account == null) throw new AccountNotFoundException();
        if (account.isBlocked()) throw new AccountBlockedException();
    }

    protected void checkIfSwipedValid(Account account) {
        if (account == null) throw new SwipedNotFoundException();
        if (account.isBlocked()) throw new SwipedBlockedException();
    }
}
