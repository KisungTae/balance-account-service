package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountBlockedException extends BaseException {

    private static final String ACCOUNT_BLOCKED_EXCEPTION = "account.blocked.exception";

    public AccountBlockedException() { super(ACCOUNT_BLOCKED_EXCEPTION);}
}
