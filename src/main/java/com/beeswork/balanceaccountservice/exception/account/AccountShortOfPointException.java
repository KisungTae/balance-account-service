package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountShortOfPointException extends BaseException {

    private static final String CODE = "account.short.of.point.exception";

    public AccountShortOfPointException() {
        super(CODE);
    }
}
