package com.beeswork.balanceaccountservice.exception;

public class BadRequestException extends BaseException {

    private static final String CODE = "bad.request.exception";

    public BadRequestException() {
        super(CODE);
    }
}
