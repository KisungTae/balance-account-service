package com.beeswork.balanceaccountservice.exception.profile;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class ProfileNotFoundException extends BaseException {
    public ProfileNotFoundException() {
        super("profile.not.found.exception");
    }
}
