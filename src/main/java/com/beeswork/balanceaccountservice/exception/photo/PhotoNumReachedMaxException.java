package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoNumReachedMaxException extends BaseException {

    private static final String PHOTO_NUM_REACHED_MAX_EXCEPTION = "photo.num.reached.max.exception";

    public PhotoNumReachedMaxException() {
        super(PHOTO_NUM_REACHED_MAX_EXCEPTION);
    }
}
