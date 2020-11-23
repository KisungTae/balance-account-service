package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccountQuestionNotFoundException extends BaseException {

    private static final String ACCOUNT_QUESTION_NOT_FOUND_EXCEPTION = "account.question.not.found.exception";

    public AccountQuestionNotFoundException() {
        super(ACCOUNT_QUESTION_NOT_FOUND_EXCEPTION);
    }
}
