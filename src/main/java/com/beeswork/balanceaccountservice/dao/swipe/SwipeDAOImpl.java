package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.swipe.QSwipeDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.swipe.QSwipe;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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
    public Swipe findBySwiperIdAndSwipedId(UUID swiperId, UUID swipedId, boolean writeLock) {
        JPAQuery<Swipe> query = jpaQueryFactory.selectFrom(qSwipe)
                                               .where(qSwipe.swiper.id.eq(swiperId).and(qSwipe.swiped.id.eq(swipedId)));
        if (writeLock) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        return query.fetchFirst();
    }

    public List<SwipeDTO> findClicks(UUID swipedId, int startPosition, int loadSize) {
        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.id,
                                                    qSwipe.swiper.id,
                                                    qSwipe.swiped.id,
                                                    qAccount.name,
                                                    qAccount.profilePhotoKey,
                                                    qSwipe.clicked,
                                                    qAccount.deleted))
                              .from(qSwipe)
                              .leftJoin(qAccount).on(qSwipe.swiper.id.eq(qAccount.id))
                              .where(qSwipe.swiped.id.eq(swipedId)
                                                     .and(qSwipe.matched.eq(false)))
                              .orderBy(qSwipe.id.desc())
                              .limit(loadSize)
                              .offset(startPosition)
                              .fetch();
    }

    @Override
    public List<SwipeDTO> findClicks(UUID swipedId, UUID lastSwiperId, int loadSize) {
        BooleanExpression condition = qSwipe.swiped.id.eq(swipedId)
                                                      .and(qAccount.deleted.eq(false))
                                                      .and(qSwipe.matched.eq(false));
        if (lastSwiperId != null) {
            JPQLQuery<Long> lastSwipeId = JPAExpressions.select(qSwipe.id)
                                                        .from(qSwipe)
                                                        .where(qSwipe.swiper.id.eq(lastSwiperId).and(qSwipe.swiped.id.eq(swipedId)));
            condition = condition.and(qSwipe.id.lt(lastSwipeId));
        }
        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.id,
                                                    qSwipe.swiper.id,
                                                    qSwipe.swiped.id,
                                                    qAccount.name,
                                                    qAccount.profilePhotoKey,
                                                    qSwipe.clicked))
                              .from(qSwipe)
                              .leftJoin(qAccount).on(qSwipe.swiper.id.eq(qAccount.id))
                              .where(condition)
                              .orderBy(qSwipe.id.desc())
                              .limit(loadSize)
                              .fetch();
    }

    @Override
    public long countClicks(UUID swipedId) {
        return jpaQueryFactory.selectFrom(qSwipe)
                       .leftJoin(qAccount).on(qSwipe.swiper.id.eq(qAccount.id))
                       .where(qSwipe.swiped.id.eq(swipedId)
                                              .and(qSwipe.matched.eq(false))
                                              .and(qAccount.deleted.eq(false)))
                       .fetchCount();
    }


}
