package com.beeswork.balanceaccountservice.exception.match;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class MatchExistsException extends BaseException {

    public MatchExistsException() {
        super(ExceptionCode.MATCH_EXISTS_EXCEPTION);
    }
}
