package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.match.QMatch;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import com.beeswork.balanceaccountservice.projection.QMatchProjection;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class MatchDAOImpl extends BaseDAOImpl<Match> implements MatchDAO {

    private final QMatch qMatch = QMatch.match;
    private final QAccount qAccount = QAccount.account;

    @Autowired
    public MatchDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {

        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Match findWithAccounts(UUID matcherId, UUID matchedId, Long chatId) {

        return jpaQueryFactory.selectFrom(qMatch)
                              .innerJoin(qMatch.matcher, qAccount)
                              .innerJoin(qMatch.matched, qAccount)
                              .where(qMatch.matcherId.eq(matcherId)
                                                     .and(qMatch.matchedId.eq(matchedId))
                                                     .and(qMatch.chatId.eq(chatId))).fetchOne();
    }

    @Override
    public List<MatchProjection> findAllAfter(UUID matcherId, Date fetchedAt) {

        fetchedAt = DateUtils.addMilliseconds(fetchedAt, -1);

        Expression<Date> updatedAtCase = new CaseBuilder().when(qMatch.updatedAt.after(qAccount.updatedAt))
                                                          .then(qMatch.updatedAt)
                                                          .otherwise(qAccount.updatedAt);

        return jpaQueryFactory.select(new QMatchProjection(qMatch.chatId,
                                                           qMatch.matchedId,
                                                           qAccount.name,
                                                           qAccount.repPhotoKey,
                                                           qMatch.unmatched,
                                                           qAccount.blocked,
                                                           qAccount.deleted,
                                                           updatedAtCase))
                              .from(qMatch)
                              .leftJoin(qAccount)
                              .on(qMatch.matchedId.eq(qAccount.id))
                              .where(qMatch.matcherId.eq(matcherId)
                                                     .and(qMatch.updatedAt.after(fetchedAt)
                                                                          .or(qAccount.updatedAt.after(fetchedAt))))
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
