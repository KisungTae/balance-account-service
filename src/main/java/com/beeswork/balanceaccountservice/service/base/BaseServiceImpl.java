package com.beeswork.balanceaccountservice.service.base;

import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountDeletedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedDeletedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.UUID;

public abstract class BaseServiceImpl {

    private static final int FETCH_OFFSET_IN_MINUTES = -5;

    protected Account validateAccount(Account account, UUID identityToken) {
        validateAccount(account);
        if (!account.getIdentityToken().equals(identityToken)) throw new AccountNotFoundException();
        return account;
    }

    protected void validateAccount(Account account) {
        if (account == null) throw new AccountNotFoundException();
        if (account.isBlocked()) throw new AccountBlockedException();
        if (account.isDeleted()) throw new AccountDeletedException();
    }

    protected Account validateSwiped(Account account) {
        if (account == null) throw new SwipedNotFoundException();
        if (account.isDeleted()) throw new SwipedDeletedException();
        return account;
    }

    protected Date offsetFetchedAt(Date date) {
        return DateUtils.addMinutes(date, FETCH_OFFSET_IN_MINUTES);
    }
}
