package com.beeswork.balanceaccountservice.exception.question;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class QuestionSetChangedException extends BaseException {
    public QuestionSetChangedException() {
        super(ExceptionCode.QUESTION_SET_CHANGED_EXCEPTION);
    }
}
