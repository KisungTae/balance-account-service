package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeClickedExistsException extends BaseException {

    public SwipeClickedExistsException() {
        super(ExceptionCode.SWIPE_CLICKED_EXISTS_EXCEPTION);
    }
}
