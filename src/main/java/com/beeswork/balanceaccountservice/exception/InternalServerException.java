package com.beeswork.balanceaccountservice.exception;

public class InternalServerException extends BaseException {

    public static final String CODE = "internal.server.exception";

    public InternalServerException() {
        super(CODE);
    }
}
