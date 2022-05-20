package com.beeswork.balanceaccountservice.exception.profile;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class NoPhotoUploadedException extends BaseException {

    private static final String CODE = "no.photo.uploaded.exception";

    public NoPhotoUploadedException() {
        super(CODE);
    }
}
