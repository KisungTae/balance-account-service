package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoExceededMaxException extends BaseException {

    private static final String PHOTO_NUM_REACHED_MAX_EXCEPTION = "photo.exceeded.max.exception";

    public PhotoExceededMaxException() {
        super(PHOTO_NUM_REACHED_MAX_EXCEPTION);
    }
}
