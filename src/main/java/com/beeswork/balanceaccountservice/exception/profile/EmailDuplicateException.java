package com.beeswork.balanceaccountservice.exception.profile;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class EmailDuplicateException extends BaseException {

    private static final String EMAIL_DUPLICATE_EXCEPTION = "email.duplicate.exception";

    public EmailDuplicateException() {
        super(EMAIL_DUPLICATE_EXCEPTION);
    }
}
