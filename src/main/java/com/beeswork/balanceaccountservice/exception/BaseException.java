package com.beeswork.balanceaccountservice.exception;

import lombok.Getter;

@Getter
public class BaseException extends Exception {
    protected String exceptionCode;

    public BaseException(String exceptionCode) { this.exceptionCode = exceptionCode; }
}
