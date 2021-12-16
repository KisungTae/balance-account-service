package com.beeswork.balanceaccountservice.exception.question;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class QuestionSetChangedException extends BaseException {

    private static final String CODE = "question.set.changed.exception";

    public QuestionSetChangedException() {
        super(CODE);
    }
}
