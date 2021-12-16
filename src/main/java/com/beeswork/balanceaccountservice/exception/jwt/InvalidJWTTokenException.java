package com.beeswork.balanceaccountservice.exception.jwt;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class InvalidJWTTokenException extends BaseException {

    private static final String CODE = "invalid.jwt.token.exception";

    public InvalidJWTTokenException() {
        super(CODE);
    }
}
