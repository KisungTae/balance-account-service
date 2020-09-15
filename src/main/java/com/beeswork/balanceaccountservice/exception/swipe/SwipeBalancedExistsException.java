package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeBalancedExistsException extends BaseException {

    public SwipeBalancedExistsException() {
        super(ExceptionCode.SWIPE_BALANCED_EXISTS_EXCEPTION);
    }
}
