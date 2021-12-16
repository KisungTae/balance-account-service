package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountQuestionNotFoundException extends BaseException {

    private static final String CODE = "account.question.not.found.exception";

    public AccountQuestionNotFoundException() {
        super(CODE);
    }
}
