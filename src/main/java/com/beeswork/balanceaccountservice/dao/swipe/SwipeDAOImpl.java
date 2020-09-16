package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.swipe.QSwipe;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class SwipeDAOImpl extends BaseDAOImpl<Swipe> implements SwipeDAO {

    private final QAccount qAccount = QAccount.account;
    private final QSwipe qSwipe = QSwipe.swipe;

    @Autowired
    public SwipeDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public boolean balancedExists(UUID swiperId, UUID swipedId) {
        return jpaQueryFactory.selectFrom(qSwipe)
                              .where(qSwipe.swiperId.eq(swiperId)
                                                    .and(qSwipe.swipedId.eq(swipedId))
                                                    .and(qSwipe.balanced.eq(true)))
                              .fetchCount() > 0;
    }

    @Override
    public Swipe findByIdWithAccounts(Long swipeId, UUID swiperId, UUID swipedId) throws SwipeNotFoundException {
        Swipe swipe = jpaQueryFactory.selectFrom(qSwipe)
                                     .innerJoin(qSwipe.swiper, qAccount).fetchJoin()
                                     .innerJoin(qSwipe.swiped, qAccount).fetchJoin()
                                     .where(qSwipe.id.eq(swipeId)
                                                     .and(qSwipe.swiperId.eq(swiperId))
                                                     .and(qSwipe.swipedId.eq(swipedId)))
                                     .fetchOne();
        if (swipe == null) throw new SwipeNotFoundException();
        return swipe;
    }

    @Override
    public boolean existsByAccountIdsAndBalanced(UUID swiperId, UUID swipedId, boolean balanced) {
        return jpaQueryFactory.selectFrom(qSwipe)
                              .where(qSwipe.swiperId.eq(swiperId)
                                                    .and(qSwipe.swipedId.eq(swipedId))
                                                    .and(qSwipe.balanced.eq(balanced)))
                              .fetchCount() > 0;
    }


}
