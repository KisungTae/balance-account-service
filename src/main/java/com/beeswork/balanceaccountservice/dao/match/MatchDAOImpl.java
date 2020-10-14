package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.entity.photo.QPhoto;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import com.beeswork.balanceaccountservice.projection.QMatchProjection;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class MatchDAOImpl extends BaseDAOImpl<Match> implements MatchDAO {

    private final QMatch qMatch = QMatch.match;
    private final QPhoto qPhoto = QPhoto.photo;
    private final QAccount qAccount = QAccount.account;

    @Autowired
    public MatchDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {

        super(entityManager, jpaQueryFactory);
    }

    @Override
    public boolean existsById(MatchId matchId) {

        return jpaQueryFactory.selectFrom(qMatch).where(qMatch.matchId.eq(matchId)).fetchCount() > 0;
    }

    @Override
    public List<MatchProjection> findAllByMatcherId(UUID matcherId, Date fetchedAt) {
        return jpaQueryFactory.select(new QMatchProjection(qMatch.matchedId, qAccount.name, qAccount.repPhotoKey, qMatch.unmatched))
                              .from(qMatch)
                              .leftJoin(qAccount)
                              .on(qMatch.matchedId.eq(qAccount.id))
                              .where(qMatch.matcherId.eq(matcherId).and(qMatch.updateAt.after(fetchedAt)))
                              .fetch();
    }

    @Override
    public List<Match> findPairById(UUID matcherId, UUID matchedId) {

        return jpaQueryFactory.selectFrom(qMatch)
                              .where(qMatch.matcherId.eq(matcherId)
                                                     .and(qMatch.matchedId.eq(matchedId))
                                                     .or(qMatch.matcherId.eq(matchedId)
                                                                         .and(qMatch.matchedId.eq(matcherId))))
                              .fetch();
    }


}
