package com.beeswork.balanceaccountservice.exception.profile;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class EmailDuplicateException extends BaseException {

    private static final String CODE = "email.duplicate.exception";

    public EmailDuplicateException() {
        super(CODE);
    }
}
