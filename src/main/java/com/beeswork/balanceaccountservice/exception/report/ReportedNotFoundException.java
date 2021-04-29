package com.beeswork.balanceaccountservice.exception.report;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class ReportedNotFoundException extends BaseException {

    static final String REPORTED_NOT_FOUND_EXCEPTION = "reported.not.found.exception";

    public ReportedNotFoundException() {
        super(REPORTED_NOT_FOUND_EXCEPTION);
    }
}
