package com.beeswork.balanceaccountservice.service.report;

import java.util.UUID;

public interface ReportService {

    void reportProfile(UUID accountId, UUID identityToken, UUID reportedId, int reportReasonId, String description);
    void reportMatch(UUID accountId, UUID identityToken, UUID reportedId, int reportReasonId, String description);
}
