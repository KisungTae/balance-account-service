package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.match.QUnmatchAudit;
import com.beeswork.balanceaccountservice.entity.match.UnmatchAudit;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class UnmatchAuditDAOImpl extends BaseDAOImpl<UnmatchAudit> implements UnmatchAuditDAO {

    private QUnmatchAudit qUnmatchAudit = QUnmatchAudit.unmatchAudit;

    @Autowired
    public UnmatchAuditDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public boolean existsBy(UUID swiperId, UUID swipedId) {
        return jpaQueryFactory.selectFrom(qUnmatchAudit)
                              .where(qUnmatchAudit.swiper.id.eq(swiperId).and(qUnmatchAudit.swiped.id.eq(swipedId)))
                              .fetchCount() > 0;
    }
}
