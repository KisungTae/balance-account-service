package com.beeswork.balanceaccountservice.exception.question;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class QuestionNotFoundException extends BaseException {

    private static final String CODE = "question.not.found.exception";

    public QuestionNotFoundException() {
        super(CODE);
    }
}
