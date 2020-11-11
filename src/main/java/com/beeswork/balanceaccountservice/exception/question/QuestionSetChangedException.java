package com.beeswork.balanceaccountservice.exception.question;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class QuestionSetChangedException extends BaseException {

    private static final String QUESTION_SET_CHANGED_EXCEPTION = "question.set.changed.exception";

    public QuestionSetChangedException() {
        super(QUESTION_SET_CHANGED_EXCEPTION);
    }
}
