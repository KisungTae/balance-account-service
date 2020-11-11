package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountNotFoundException extends BaseException {

    private static final String ACCOUNT_NOT_FOUND_EXCEPTION = "account.not.found.exception";

    public AccountNotFoundException() { super(ACCOUNT_NOT_FOUND_EXCEPTION); }
}
