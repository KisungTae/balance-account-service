package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
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
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class SwipeDAOImpl extends BaseDAOImpl<Swipe> implements SwipeDAO {

    private final QAccount qAccount = QAccount.account;
    private final QSwipe qSwipe = QSwipe.swipe;
    private final QMatch qMatch = QMatch.match;

    @Autowired
    public SwipeDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }


    @Override
    public Swipe findWithAccounts(UUID swiperId, UUID swipedId) throws SwipeNotFoundException {
        return jpaQueryFactory.selectFrom(qSwipe)
                              .innerJoin(qSwipe.swiper, qAccount).fetchJoin()
                              .innerJoin(qSwipe.swiped, qAccount).fetchJoin()
                              .where(qSwipe.swiperId.eq(swiperId).and(qSwipe.swipedId.eq(swipedId)))
                              .fetchOne();
    }

    @Override
    public boolean existsByClicked(UUID swiperId, UUID swipedId, boolean clicked) {
        return jpaQueryFactory.selectFrom(qSwipe)
                              .where(qSwipe.swiperId.eq(swiperId)
                                                    .and(qSwipe.swipedId.eq(swipedId))
                                                    .and(qSwipe.clicked.eq(clicked)))
                              .fetchCount() > 0;
    }

    @Override
    public Swipe findBy(UUID swiperId, UUID swipedId) {
        return jpaQueryFactory.selectFrom(qSwipe)
                              .where(qSwipe.swiperId.eq(swiperId).and(qSwipe.swipedId.eq(swipedId)))
                              .fetchOne();
    }

    @Override
    public List<ClickProjection> findAllClickAfter(UUID swiperId, Date fetchedAt) {
        return jpaQueryFactory.select(new QClickProjection(qSwipe.swipedId, qSwipe.updatedAt))
                              .from(qSwipe)
                              .where(qSwipe.swiperId.eq(swiperId)
                                                    .and(qSwipe.updatedAt.after(fetchedAt))
                                                    .and(qSwipe.clicked.eq(true)))
                              .fetch();
    }

    public List<ClickedProjection> findAllClickedAfter(UUID swipedId, Date fetchedAt) {
        Expression<Date> updatedAtCase = new CaseBuilder().when(qSwipe.updatedAt.after(qAccount.updatedAt))
                                                          .then(qSwipe.updatedAt)
                                                          .otherwise(qAccount.updatedAt);

        return jpaQueryFactory.select(new QClickedProjection(qSwipe.swiperId,
                                                             qAccount.repPhotoKey,
                                                             qAccount.blocked,
                                                             qAccount.deleted,
                                                             updatedAtCase))
                              .from(qSwipe)
                              .leftJoin(qMatch)
                              .on(qSwipe.swiperId.eq(qMatch.matcherId)
                                                 .and(qSwipe.swipedId.eq(qMatch.matchedId)))
                              .leftJoin(qAccount)
                              .on(qSwipe.swiperId.eq(qAccount.id))
                              .where(qSwipe.swipedId.eq(swipedId)
                                                    .and(qSwipe.clicked.eq(true))
                                                    .and(qMatch.matchedId.isNull())
                                                    .and(qSwipe.updatedAt.after(fetchedAt)
                                                                         .or(qAccount.updatedAt.after(fetchedAt))))
                              .fetch();
    }


}
