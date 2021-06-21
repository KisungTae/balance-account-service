package com.beeswork.balanceaccountservice.exception.setting;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SettingNotFoundException extends BaseException {

    private static final String SETTING_NOT_FOUND_EXCEPTION = "setting.not.found.exception";

    public SettingNotFoundException() {
        super(SETTING_NOT_FOUND_EXCEPTION);
    }
}
