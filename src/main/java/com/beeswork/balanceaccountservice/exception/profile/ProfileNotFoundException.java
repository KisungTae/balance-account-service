package com.beeswork.balanceaccountservice.exception.profile;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class ProfileNotFoundException extends BaseException {

    private static final String CODE = "profile.not.found.exception";

    public ProfileNotFoundException() {
        super(CODE);
    }
}
