package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountNotFoundException extends BaseException {

    private static final String CODE = "account.not.found.exception";

    public AccountNotFoundException() { super(CODE); }
}
