package com.beeswork.balanceaccountservice.dao.swipe;

import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.account.QPhoto;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.entity.swipe.QSwipe;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class SwipeDAOImpl extends BaseDAOImpl<Swipe> implements SwipeDAO {

    private final QAccount qAccount = QAccount.account;
    private final QSwipe qSwipe = QSwipe.swipe;
    private final QMatch qMatch = QMatch.match;
    private final QPhoto qPhoto = QPhoto.photo;

    @Autowired
    public SwipeDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public boolean clickedExists(UUID swiperId, UUID swipedId) {
        return jpaQueryFactory.selectFrom(qSwipe)
                              .where(qSwipe.swiperId.eq(swiperId)
                                                    .and(qSwipe.swipedId.eq(swipedId))
                                                    .and(qSwipe.clicked.eq(true)))
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
    public boolean existsByAccountIdsAndClicked(UUID swiperId, UUID swipedId, boolean clicked) {
        return jpaQueryFactory.selectFrom(qSwipe)
                              .where(qSwipe.swiperId.eq(swiperId)
                                                    .and(qSwipe.swipedId.eq(swipedId))
                                                    .and(qSwipe.clicked.eq(clicked)))
                              .fetchCount() > 0;
    }

//    @Override
//    public List<Swipe> findAll(UUID swiperId) {
//        return jpaQueryFactory.selectFrom(qSwipe)
//                              .leftJoin(qMatch).on(qSwipe.swiperId.eq(qMatch.matchedId))
//                              .where(qSwipe.swipedId.eq(swiperId)
//                                                    .and(qSwipe.balanced.eq(true))
//                                                    .and(qMatch.matchedId.isNull()))
//                              .fetch();
//    }

    public List<Swipe> findAllClicked(UUID swipedId) {
        List<Tuple> swipes = jpaQueryFactory.select(qSwipe.swiperId, qPhoto.key)
                                            .from(qSwipe)
                                            .leftJoin(qMatch)
                                            .on(qSwipe.swiperId.eq(qMatch.matcherId)
                                                               .and(qSwipe.swipedId.eq(qMatch.matchedId)))
                                            .leftJoin(qPhoto)
                                            .on(qSwipe.swiperId.eq(qPhoto.accountId))
                                            .where(qSwipe.swipedId.eq(swipedId)
                                                                  .and(qSwipe.clicked.eq(true))
                                                                  .and(qMatch.matchedId.isNull())
                                                                  .and(qPhoto.sequence.eq(AppConstant.REP_IMAGE_SEQUENCE)))
                                            .fetch();


        return null;
    }


}
