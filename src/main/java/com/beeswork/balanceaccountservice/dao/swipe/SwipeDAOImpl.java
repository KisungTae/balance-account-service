package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.swipe.QSwipeDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.swipe.QSwipe;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public Swipe find(UUID swiperId, UUID swipedId, boolean writeLock) {
        JPAQuery<Swipe> query = jpaQueryFactory.selectFrom(qSwipe)
                                               .where(qSwipe.swiper.id.eq(swiperId).and(qSwipe.swiped.id.eq(swipedId)));
        if (writeLock) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        return query.fetchFirst();
    }

//    public List<SwipeDTO> findAll(UUID swipedId, int startPosition, int loadSize) {
//        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.id,
//                                                    qSwipe.swiper.id,
//                                                    qSwipe.swiped.id,
//                                                    qAccount.profilePhotoKey,
//                                                    qSwipe.clicked,
//                                                    qAccount.deleted))
//                              .from(qSwipe)
//                              .leftJoin(qAccount).on(qSwipe.swiper.id.eq(qAccount.id))
//                              .where(qSwipe.swiped.id.eq(swipedId)
//                                                     .and(qSwipe.matched.eq(false)))
//                              .orderBy(qSwipe.id.desc())
//                              .limit(loadSize)
//                              .offset(startPosition)
//                              .fetch();
//    }
//
//    @Override
//    public List<SwipeDTO> findAll(UUID swipedId, Long lastSwipeId, int loadSize) {
//        BooleanExpression condition = qSwipe.swiped.id.eq(swipedId)
//                                                      .and(qAccount.deleted.eq(false))
//                                                      .and(qSwipe.matched.eq(false));
//        if (lastSwipeId != null) {
//            condition = condition.and(qSwipe.id.lt(lastSwipeId));
//        }
//        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.id,
//                                                    qSwipe.swiper.id,
//                                                    qSwipe.swiped.id,
//                                                    qAccount.profilePhotoKey,
//                                                    qSwipe.clicked))
//                              .from(qSwipe)
//                              .leftJoin(qAccount).on(qSwipe.swiper.id.eq(qAccount.id))
//                              .where(condition)
//                              .orderBy(qSwipe.id.desc())
//                              .limit(loadSize)
//                              .fetch();
//    }

    @Override
    public List<SwipeDTO> findAll(UUID swipedId, Long loadKey, int loadSize, boolean isAppend, boolean isIncludeLoadKey) {
        BooleanExpression whereCondition = qSwipe.swiped.id.eq(swipedId)
                                                      .and(qAccount.deleted.eq(false))
                                                      .and(qSwipe.matched.eq(false));

        if (loadKey != null) {
            if (isAppend) {
                whereCondition = isIncludeLoadKey ? whereCondition.and(qSwipe.id.loe(loadKey)) : whereCondition.and(qSwipe.id.lt(loadKey));
            } else {
                whereCondition = isIncludeLoadKey ? whereCondition.and(qSwipe.id.goe(loadKey)) : whereCondition.and(qSwipe.id.gt(loadKey));
            }
        }

        OrderSpecifier<Long> orderByCondition = isAppend ? qSwipe.id.desc() : qSwipe.id.asc();

        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.id,
                                                    qSwipe.swiper.id,
                                                    qSwipe.swiped.id,
                                                    qSwipe.swiped.name,
                                                    qSwipe.clicked,
                                                    qAccount.profilePhotoKey))
                              .from(qSwipe)
                              .leftJoin(qAccount).on(qSwipe.swiper.id.eq(qAccount.id))
                              .where(whereCondition)
                              .orderBy(orderByCondition)
                              .limit(loadSize)
                              .fetch();
    }

    @Override
    public long countSwipes(UUID swipedId) {
        return jpaQueryFactory.selectFrom(qSwipe)
                              .leftJoin(qAccount).on(qSwipe.swiper.id.eq(qAccount.id))
                              .where(qSwipe.swiped.id.eq(swipedId)
                                                     .and(qSwipe.matched.eq(false))
                                                     .and(qAccount.deleted.eq(false)))
                              .fetchCount();
    }


}
