package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountEmailNotMutableException extends BaseException {

    private static final String ACCOUNT_EMAIL_NOT_MUTABLE_EXCEPTION = "account.email.not.mutable.exception";
    public AccountEmailNotMutableException() {
        super(ACCOUNT_EMAIL_NOT_MUTABLE_EXCEPTION);
    }
}
