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
import org.modelmapper.ModelMapper;
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
    private final MatchDAO        matchDAO;

    @Autowired
    public ReportServiceImpl(ReportDAO reportDAO,
                             ReportReasonDAO reportReasonDAO,
                             AccountDAO accountDAO,
                             MatchDAO matchDAO) {
        this.reportDAO = reportDAO;
        this.reportReasonDAO = reportReasonDAO;
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
    }


    @Override
    @Transactional
    public void reportProfile(UUID accountId, UUID identityToken, UUID reportedId, int reportReasonId, String description) {
        createReport(accountId, identityToken, reportedId, reportReasonId, description);
    }

    @Override
    @Transactional
    public void reportMatch(UUID accountId, UUID identityToken, UUID reportedId, int reportReasonId, String description) {
        createReport(accountId, identityToken, reportedId, reportReasonId, description);
        Match reporterMatch = matchDAO.findById(accountId, reportedId);
        Match reportedMatch = matchDAO.findById(reportedId, accountId);

        if (reporterMatch == null || reportedMatch == null) throw new MatchNotFoundException();

        Date updatedAt = new Date();
        reporterMatch.setupAsUnmatcher(updatedAt);
        reportedMatch.setupAsUnmatched(updatedAt);
    }

    private void createReport(UUID accountId, UUID identityToken, UUID reportedId, int reportReasonId, String description) {
        ReportReason reportReason = reportReasonDAO.findById(reportReasonId);
        if (reportReason == null) throw new ReportReasonNotFoundException();

        Account reporter = accountDAO.findById(accountId);
        validateAccount(reporter, identityToken);

        Account reported = accountDAO.findById(reportedId);
        if (reported == null) throw new ReportedNotFoundException();

        Report report = new Report(reporter, reported, reportReason, description, new Date());
        reportDAO.persist(report);
    }
}
