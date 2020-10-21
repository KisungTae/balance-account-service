package com.beeswork.balanceaccountservice.exception;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;

public class BadRequestException extends BaseException {

    public BadRequestException() {
        super(ExceptionCode.BAD_REQUEST_EXCEPTION);
    }
}
