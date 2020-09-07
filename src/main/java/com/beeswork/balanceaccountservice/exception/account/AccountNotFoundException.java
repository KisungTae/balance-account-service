package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountNotFoundException extends BaseException {
    public AccountNotFoundException() { super(ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION); }
}
