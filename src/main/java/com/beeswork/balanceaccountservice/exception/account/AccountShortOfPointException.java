package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountShortOfPointException extends BaseException {

    public AccountShortOfPointException() {
        super(ExceptionCode.ACCOUNT_SHORT_OF_POINT_EXCEPTION);
    }
}
