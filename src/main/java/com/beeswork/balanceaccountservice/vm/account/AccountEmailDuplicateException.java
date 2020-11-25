package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountEmailDuplicateException extends BaseException {

    private static final String ACCOUNT_EMAIL_DUPLICATE_EXCEPTION = "account.email.duplicate.exception";

    public AccountEmailDuplicateException() {
        super(ACCOUNT_EMAIL_DUPLICATE_EXCEPTION);
    }
}
