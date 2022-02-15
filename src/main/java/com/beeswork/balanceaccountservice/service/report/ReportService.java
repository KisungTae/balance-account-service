package com.beeswork.balanceaccountservice.service.report;

import java.util.Date;
import java.util.UUID;

public interface ReportService {
    void createReport(UUID reporterId, UUID reportedId, int reportReasonId, String description);
    void createReport(UUID reporterId, UUID reportedId, int reportReasonId, String description, Date createdAt);
}
