package com.beeswork.balanceaccountservice.service.report;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.report.ReportDAO;
import com.beeswork.balanceaccountservice.dao.report.ReportReasonDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.report.Report;
import com.beeswork.balanceaccountservice.entity.report.ReportReason;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.report.ReportReasonNotFoundException;
import com.beeswork.balanceaccountservice.exception.report.ReportedNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class ReportServiceImpl extends BaseServiceImpl implements ReportService {

    private final ReportDAO       reportDAO;
    private final ReportReasonDAO reportReasonDAO;
    private final AccountDAO      accountDAO;

    @Autowired
    public ReportServiceImpl(ReportDAO reportDAO,
                             ReportReasonDAO reportReasonDAO,
                             AccountDAO accountDAO) {
        this.reportDAO = reportDAO;
        this.reportReasonDAO = reportReasonDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional
    public void createReport(UUID reporterId, UUID reportedId, int reportReasonId, String description) {
        createReport(reporterId, reportedId, reportReasonId, description, new Date());
    }

    @Override
    @Transactional
    public void createReport(UUID reporterId, UUID reportedId, int reportReasonId, String description, Date createdAt) {
        ReportReason reportReason = reportReasonDAO.findById(reportReasonId);
        if (reportReason == null) {
            throw new ReportReasonNotFoundException();
        }
        Account reporter = accountDAO.findById(reporterId);
        Account reported = accountDAO.findById(reportedId);
        if (reported == null) {
            throw new ReportedNotFoundException();
        }
        Report report = new Report(reporter, reported, reportReason, description, createdAt);
        reportDAO.persist(report);
    }
}
