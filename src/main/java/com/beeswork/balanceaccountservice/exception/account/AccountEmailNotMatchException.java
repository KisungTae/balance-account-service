package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountEmailNotMatchException extends BaseException {

    public AccountEmailNotMatchException() {
        super(ExceptionCode.ACCOUNT_EMAIL_NOT_MATCH_EXCEPTION);
    }
}
