package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoInvalidDeleteException extends BaseException {

    private static final String CODE = "photo.invalid.delete.exception";

    public PhotoInvalidDeleteException() {
        super(CODE);
    }
}
