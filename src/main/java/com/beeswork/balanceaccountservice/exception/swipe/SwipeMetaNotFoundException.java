package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeMetaNotFoundException extends BaseException {
    public static final String CODE = "swipe.meta.not.found.exception";
    public SwipeMetaNotFoundException() {
        super(CODE);
    }
}
