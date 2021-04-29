package com.beeswork.balanceaccountservice.exception.report;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class ReportReasonNotFoundException extends BaseException {

    private static final String REPORT_NOT_FOUND_EXCEPTION = "report.not.found.exception";

    public ReportReasonNotFoundException() {
        super(REPORT_NOT_FOUND_EXCEPTION);
    }
}
