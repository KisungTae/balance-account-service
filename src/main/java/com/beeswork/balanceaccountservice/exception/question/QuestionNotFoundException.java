package com.beeswork.balanceaccountservice.exception.question;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class QuestionNotFoundException extends BaseException {

    private static final String QUESTION_NOT_FOUND_EXCEPTION = "question.not.found.exception";

    public QuestionNotFoundException() {
        super(QUESTION_NOT_FOUND_EXCEPTION);
    }
}
