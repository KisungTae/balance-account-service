package com.beeswork.balanceaccountservice.exception;

public class BadRequestException extends BaseException {

    public static final String CODE = "bad.request.exception";

    public BadRequestException() {
        super(CODE);
    }
}
