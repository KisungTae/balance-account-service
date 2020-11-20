package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoNotFoundException extends BaseException {

    private static final String PHOTO_NOT_FOUND_EXCEPTION = "photo.not.found.exception";

    public PhotoNotFoundException() {
        super(PHOTO_NOT_FOUND_EXCEPTION);
    }
}
