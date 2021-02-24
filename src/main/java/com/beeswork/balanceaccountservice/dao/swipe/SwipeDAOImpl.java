package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.swipe.QSwipeDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeId;
import com.beeswork.balanceaccountservice.projection.ClickProjection;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.entity.swipe.QSwipe;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.projection.QClickProjection;
import com.beeswork.balanceaccountservice.projection.QClickedProjection;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class SwipeDAOImpl extends BaseDAOImpl<Swipe> implements SwipeDAO {

    private final QAccount qAccount = QAccount.account;
    private final QSwipe   qSwipe   = QSwipe.swipe;

    @Autowired
    public SwipeDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Swipe findById(SwipeId swipeId, boolean writeLock) {
        if (writeLock) return entityManager.find(Swipe.class, swipeId, LockModeType.PESSIMISTIC_WRITE);
        else return jpaQueryFactory.selectFrom(qSwipe).where(qSwipe.swipeId.eq(swipeId)).fetchOne();
    }

    @Override
    public List<SwipeDTO> findAllClickAfter(UUID accountId, Date fetchedAt) {
        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.swipedId, qSwipe.updatedAt))
                              .from(qSwipe)
                              .where(qSwipe.swiperId.eq(accountId)
                                                    .and(qSwipe.updatedAt.after(fetchedAt))
                                                    .and(qSwipe.clicked.eq(true))
                                                    .and(qSwipe.matched.eq(false)))
                              .fetch();
    }

    public List<SwipeDTO> findAllClickedAfter(UUID accountId, Date fetchedAt) {
        Expression<Date> updatedAtCase = new CaseBuilder().when(qSwipe.updatedAt.after(qAccount.updatedAt))
                                                          .then(qSwipe.updatedAt)
                                                          .otherwise(qAccount.updatedAt);

        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.swiperId,
                                                    qAccount.repPhotoKey,
                                                    qAccount.deleted,
                                                    updatedAtCase))
                              .from(qSwipe)
                              .leftJoin(qAccount).on(qSwipe.swiperId.eq(qAccount.id))
                              .where(qSwipe.swipedId.eq(accountId)
                                                    .and(qSwipe.clicked.eq(true))
                                                    .and(qSwipe.matched.eq(false))
                                                    .and(qSwipe.updatedAt.after(fetchedAt)
                                                                         .or(qAccount.updatedAt.after(fetchedAt))))
                              .fetch();
    }


}
