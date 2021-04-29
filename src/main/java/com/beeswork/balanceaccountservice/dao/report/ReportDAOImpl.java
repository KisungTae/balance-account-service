package com.beeswork.balanceaccountservice.dao.report;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.report.QReport;
import com.beeswork.balanceaccountservice.entity.report.Report;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ReportDAOImpl extends BaseDAOImpl<Report> implements ReportDAO {

    private final QReport qReport = QReport.report;

    @Autowired
    public ReportDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }
}
