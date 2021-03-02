package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class EmailNotMutableException extends BaseException {

    private static final String EMAIL_NOT_MUTABLE_EXCEPTION = "email.not.mutable.exception";

    public EmailNotMutableException() {
        super(EMAIL_NOT_MUTABLE_EXCEPTION);
    }
}
