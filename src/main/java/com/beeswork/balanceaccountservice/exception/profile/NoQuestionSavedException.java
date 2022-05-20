package com.beeswork.balanceaccountservice.exception.profile;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class NoQuestionSavedException extends BaseException {

    private static final String CODE = "no.question.saved.exception";

    public NoQuestionSavedException() {
        super(CODE);
    }
}
