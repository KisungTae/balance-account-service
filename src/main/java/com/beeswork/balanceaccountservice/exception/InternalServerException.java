package com.beeswork.balanceaccountservice.exception;

public class InternalServerException extends BaseException {

    private static final String CODE = "internal.server.exception";

    public InternalServerException() {
        super(CODE);
    }
}
