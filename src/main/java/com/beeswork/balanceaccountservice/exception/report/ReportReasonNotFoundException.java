package com.beeswork.balanceaccountservice.exception.report;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class ReportReasonNotFoundException extends BaseException {

    private static final String CODE = "report.not.found.exception";

    public ReportReasonNotFoundException() {
        super(CODE);
    }
}
