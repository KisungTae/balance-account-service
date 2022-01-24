package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.swipe.QSwipeDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeId;
import com.beeswork.balanceaccountservice.entity.swipe.QSwipe;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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
    public Swipe findById(SwipeId swipeId) {
        return jpaQueryFactory.selectFrom(qSwipe).where(qSwipe.swipeId.eq(swipeId)).fetchFirst();
    }

    @Override
    public Swipe findByIdWithLock(SwipeId swipeId) {
        return entityManager.find(Swipe.class, swipeId, LockModeType.PESSIMISTIC_WRITE);
    }

    public List<SwipeDTO> findClicks(UUID accountId, int loadSize, int startPosition) {
        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.swipeId.swiperId,
                                                    qAccount.name,
                                                    qAccount.profilePhotoKey,
                                                    qAccount.deleted,
                                                    qSwipe.updatedAt))
                              .from(qSwipe)
                              .leftJoin(qAccount).on(qSwipe.swipeId.swiperId.eq(qAccount.id))
                              .where(qSwipe.swipeId.swipedId.eq(accountId)
                                                            .and(qSwipe.clicked.eq(true))
                                                            .and(qSwipe.matched.eq(false)))
                              .orderBy(qSwipe.updatedAt.desc())
                              .limit(loadSize)
                              .offset(startPosition)
                              .fetch();
//        Expression<Date> updatedAtCase = new CaseBuilder().when(qSwipe.updatedAt.after(qAccount.updatedAt))
//                                                          .then(qSwipe.updatedAt)
//                                                          .otherwise(qAccount.updatedAt);
//
//        return jpaQueryFactory.select(new QSwipeDTO(qSwipe.swipeId.swiperId,
//                                                    qAccount.name,
//                                                    qAccount.profilePhotoKey,
//                                                    qAccount.deleted,
//                                                    updatedAtCase))
//                              .from(qSwipe)
//                              .leftJoin(qAccount).on(qSwipe.swipeId.swiperId.eq(qAccount.id))
//                              .where(qSwipe.swipeId.swipedId.eq(accountId)
//                                                    .and(qSwipe.clicked.eq(true))
//                                                    .and(qSwipe.matched.eq(false))
//                                                    .and(qSwipe.updatedAt.after(loadSize)
//                                                                         .or(qAccount.updatedAt.after(loadSize))))
//                              .fetch();
    }


}
