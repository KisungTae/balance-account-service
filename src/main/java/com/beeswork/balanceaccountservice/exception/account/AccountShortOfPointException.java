package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountShortOfPointException extends BaseException {

    private static final String ACCOUNT_SHORT_OF_POINT_EXCEPTION = "account.short.of.point.exception";

    public AccountShortOfPointException() {
        super(ACCOUNT_SHORT_OF_POINT_EXCEPTION);
    }
}
