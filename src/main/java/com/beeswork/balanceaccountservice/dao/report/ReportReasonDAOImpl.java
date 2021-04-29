package com.beeswork.balanceaccountservice.dao.report;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.report.QReportReason;
import com.beeswork.balanceaccountservice.entity.report.ReportReason;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ReportReasonDAOImpl extends BaseDAOImpl<ReportReason> implements ReportReasonDAO {

    private final QReportReason qReportReason = QReportReason.reportReason;

    @Autowired
    public ReportReasonDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public ReportReason findById(int id) {
        return jpaQueryFactory.selectFrom(qReportReason).where(qReportReason.id.eq(id)).fetchFirst();
    }
}
