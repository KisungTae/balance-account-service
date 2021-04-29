package com.beeswork.balanceaccountservice.dao.report;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.report.ReportReason;

public interface ReportReasonDAO extends BaseDAO<ReportReason> {

    ReportReason findById(int id);
}
