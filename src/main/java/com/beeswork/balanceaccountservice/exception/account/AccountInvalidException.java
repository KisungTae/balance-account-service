package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountInvalidException extends BaseException {
    public AccountInvalidException() {
        super(ExceptionCode.ACCOUNT_INVALID_EXCEPTION);
    }
}
