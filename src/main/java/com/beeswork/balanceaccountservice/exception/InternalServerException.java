package com.beeswork.balanceaccountservice.exception;

public class InternalServerException extends BaseException {

    private static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";

    public InternalServerException() {
        super(INTERNAL_SERVER_EXCEPTION);
    }
}
