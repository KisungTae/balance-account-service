package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeMetaNotFoundException extends BaseException {
    public static final String SWIPE_META_NOT_FOUND_EXCEPTION = "swipe.meta.not.found.exception";
    public SwipeMetaNotFoundException() {
        super(SWIPE_META_NOT_FOUND_EXCEPTION);
    }
}
