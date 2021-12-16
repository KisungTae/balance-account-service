package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountBlockedException extends BaseException {

    private static final String CODE = "account.blocked.exception";

    public AccountBlockedException() { super(CODE);}
}
