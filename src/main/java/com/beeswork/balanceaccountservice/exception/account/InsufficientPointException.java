package com.beeswork.balanceaccountservice.exception.account;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class InsufficientPointException extends BaseException {

    private static final String CODE = "insufficient.point.exception";

    public InsufficientPointException() {
        super(CODE);
    }
}
