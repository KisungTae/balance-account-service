package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountDeletedException extends BaseException {

    private static final String ACCOUNT_DELETED_EXCEPTION = "account.deleted.exception";

    public AccountDeletedException() {
        super(ACCOUNT_DELETED_EXCEPTION);
    }
}
