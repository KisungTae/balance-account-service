package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedNotFoundException extends BaseException {

    public SwipedNotFoundException() {
        super(ExceptionCode.SWIPE_NOT_FOUND_EXCEPTION);
    }
}
