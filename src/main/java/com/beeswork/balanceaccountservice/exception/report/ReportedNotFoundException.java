package com.beeswork.balanceaccountservice.exception.report;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class ReportedNotFoundException extends BaseException {

    private static final String CODE = "reported.not.found.exception";

    public ReportedNotFoundException() {
        super(CODE);
    }
}
