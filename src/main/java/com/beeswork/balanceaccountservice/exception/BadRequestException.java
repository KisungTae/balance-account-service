package com.beeswork.balanceaccountservice.exception;

public class BadRequestException extends BaseException {

    public static final String BAD_REQUEST_EXCEPTION = "bad.request.exception";

    public BadRequestException() {
        super(BAD_REQUEST_EXCEPTION);
    }
}
