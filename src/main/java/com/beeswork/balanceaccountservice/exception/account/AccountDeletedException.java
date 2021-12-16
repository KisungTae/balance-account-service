package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountDeletedException extends BaseException {

    private static final String CODE = "account.deleted.exception";

    public AccountDeletedException() {
        super(CODE);
    }
}
