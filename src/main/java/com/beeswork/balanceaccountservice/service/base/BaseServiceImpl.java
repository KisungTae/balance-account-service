package com.beeswork.balanceaccountservice.service.base;

import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountDeletedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedBlockedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedDeletedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.UUID;

public abstract class BaseServiceImpl {

    protected final ModelMapper modelMapper;
    private static final int FETCH_OFFSET_IN_MINUTES = -5;


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
        if (account.isDeleted()) throw new SwipedDeletedException();
    }

    protected Date offsetFetchedDate(Date date) {
        return DateUtils.addMinutes(date, FETCH_OFFSET_IN_MINUTES);
    }
}
