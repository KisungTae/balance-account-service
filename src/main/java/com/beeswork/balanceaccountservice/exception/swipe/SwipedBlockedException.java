package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedBlockedException extends BaseException {

    public SwipedBlockedException() {
        super(ExceptionCode.SWIPED_BLOCKED_EXCEPTION);
    }
}
