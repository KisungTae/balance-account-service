package com.beeswork.balanceaccountservice.exception.setting;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SettingNotFoundException extends BaseException {

    private static final String CODE = "setting.not.found.exception";

    public SettingNotFoundException() {
        super(CODE);
    }
}
