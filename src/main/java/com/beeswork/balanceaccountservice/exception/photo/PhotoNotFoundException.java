package com.beeswork.balanceaccountservice.exception.photo;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class PhotoNotFoundException extends BaseException {

    private static final String CODE = "photo.not.found.exception";

    public PhotoNotFoundException() {
        super(CODE);
    }
}
