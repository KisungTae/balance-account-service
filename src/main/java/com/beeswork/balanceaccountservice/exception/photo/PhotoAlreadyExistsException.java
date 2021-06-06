package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoAlreadyExistsException extends BaseException {

    private static final String PHOTO_ALREADY_EXISTS_EXCEPTION = "photo.already.exists.exception";

    public PhotoAlreadyExistsException() { super(PHOTO_ALREADY_EXISTS_EXCEPTION); }
}
