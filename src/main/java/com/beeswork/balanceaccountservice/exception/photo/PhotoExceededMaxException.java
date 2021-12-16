package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoExceededMaxException extends BaseException {

    private static final String CODE = "photo.exceeded.max.exception";

    public PhotoExceededMaxException() {
        super(CODE);
    }
}
