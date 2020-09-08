package com.beeswork.balanceaccountservice.exception.question;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class QuestionNotFoundException extends BaseException {

    public QuestionNotFoundException() {
        super(ExceptionCode.QUESTION_NOT_FOUND_EXCEPTION);
    }
}
