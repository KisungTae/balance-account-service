package com.beeswork.balanceaccountservice.service.base;

import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedBlockedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import org.modelmapper.ModelMapper;

import java.util.UUID;

public abstract class BaseServiceImpl {

    protected final ModelMapper modelMapper;

    public BaseServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    protected void checkIfAccountValid(Account account, UUID identityToken) {
        checkIfAccountValid(account);
        if (!account.getIdentityToken().equals(identityToken)) throw new AccountNotFoundException();
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
