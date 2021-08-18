package com.beeswork.balanceaccountservice.service.report;

import java.util.UUID;

public interface ReportService {

    void reportProfile(UUID accountId, UUID reportedId, int reportReasonId, String description);
    void reportMatch(UUID accountId, UUID reportedId, int reportReasonId, String description);
}
