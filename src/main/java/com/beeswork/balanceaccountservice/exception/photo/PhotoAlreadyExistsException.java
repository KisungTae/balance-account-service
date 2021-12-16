package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoAlreadyExistsException extends BaseException {

    private static final String CODE = "photo.already.exists.exception";

    public PhotoAlreadyExistsException() { super(CODE); }
}
