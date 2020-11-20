package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoInvalidDeleteException extends BaseException {

    private static final String PHOTO_INVALID_DELETE_EXCEPTION = "photo.invalid.delete.exception";

    public PhotoInvalidDeleteException() {
        super(PHOTO_INVALID_DELETE_EXCEPTION);
    }
}
