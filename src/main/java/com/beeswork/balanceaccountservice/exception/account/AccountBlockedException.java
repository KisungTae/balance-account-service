package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountBlockedException extends BaseException {
    public AccountBlockedException() { super(ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION);}
}
