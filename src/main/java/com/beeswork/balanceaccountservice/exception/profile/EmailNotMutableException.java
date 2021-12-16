package com.beeswork.balanceaccountservice.exception.profile;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class EmailNotMutableException extends BaseException {

    private static final String CODE = "email.not.mutable.exception";

    public EmailNotMutableException() {
        super(CODE);
    }
}
