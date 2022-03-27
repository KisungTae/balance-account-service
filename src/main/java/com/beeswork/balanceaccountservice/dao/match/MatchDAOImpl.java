package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.match.QMatchDTO;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.QMatch;;
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
public class MatchDAOImpl extends BaseDAOImpl<Match> implements MatchDAO {

    private final QMatch   qMatch   = QMatch.match;
    private final QAccount qAccount = QAccount.account;

    @Autowired
    public MatchDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public List<MatchDTO> findAllBy(UUID swiperId, Long lastMatchId, int loadSize, MatchPageFilter matchPageFilter) {
        BooleanExpression condition = matchPageCondition(swiperId, matchPageFilter);
        if (lastMatchId != null) {
            condition = condition.and(qMatch.id.lt(lastMatchId));
        }
        return selectMatchDTO().from(qMatch)
                               .leftJoin(qAccount).on(qAccount.id.eq(qMatch.swiped.id))
                               .where(condition)
                               .orderBy(qMatch.id.desc())
                               .limit(loadSize)
                               .fetch();
    }

    @Override
    public List<MatchDTO> findAllBy(UUID swiperId, int startPosition, int loadSize, MatchPageFilter matchPageFilter) {
        return selectMatchDTO().from(qMatch)
                               .leftJoin(qAccount).on(qAccount.id.eq(qMatch.swiped.id))
                               .where(matchPageCondition(swiperId, matchPageFilter))
                               .orderBy(qMatch.id.desc())
                               .limit(loadSize)
                               .offset(startPosition).fetch();
    }

    private JPAQuery<MatchDTO> selectMatchDTO() {
        return jpaQueryFactory.select(new QMatchDTO(qMatch.id,
                                                    qMatch.chatId,
                                                    qMatch.swiper.id,
                                                    qMatch.swiped.id,
                                                    qMatch.unmatched,
                                                    qMatch.lastReceivedChatMessageId,
                                                    qMatch.lastReadReceivedChatMessageId,
                                                    qMatch.lastReadByChatMessageId,
                                                    qMatch.lastChatMessageId,
                                                    qMatch.lastChatMessageBody,
                                                    qMatch.createdAt,
                                                    qAccount.name,
                                                    qAccount.profilePhotoKey,
                                                    qAccount.deleted));
    }

    private BooleanExpression matchPageCondition(UUID swiperId, MatchPageFilter matchPageFilter) {
        BooleanExpression condition = qMatch.swiper.id.eq(swiperId).and(qMatch.deleted.eq(false));
        if (matchPageFilter != null) {
            switch (matchPageFilter) {
                case MATCH:
                    condition = condition.and(qMatch.lastChatMessageId.eq(0L));
                    break;
                case CHAT:
                    condition = condition.and(qMatch.lastChatMessageId.gt(0L));
                    break;
                case CHAT_WITH_MESSAGES:
                    condition = condition.and(qMatch.lastReadReceivedChatMessageId.lt(qMatch.lastReceivedChatMessageId));
                    break;
            }
            condition.and(qMatch.unmatched.eq(false).and(qAccount.deleted.eq(false)));
        }
        return condition;
    }

    @Override
    public Match findBy(UUID swiperId, UUID swipedId, boolean writeLock) {
        JPAQuery<Match> query = jpaQueryFactory.selectFrom(qMatch).where(qMatch.swiper.id.eq(swiperId).and(qMatch.swiped.id.eq(swipedId)));
        if (writeLock) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        return query.fetchFirst();
    }

    @Override
    public long countMatchesBy(UUID swiperId) {
        return jpaQueryFactory.selectFrom(qMatch).where(matchPageCondition(swiperId, MatchPageFilter.MATCH)).fetchCount();
    }

    @Override
    public boolean existsBy(UUID swiperId, UUID chatId) {
        return jpaQueryFactory.selectFrom(qMatch).where(qMatch.swiper.id.eq(swiperId).and(qMatch.chatId.eq(chatId))).fetchCount() > 0;
    }

    @Override
    public List<Match> findAllBy(UUID chatId, boolean writeLock) {
//      NOTE 1. orderBy is for deadlock
        JPAQuery<Match> query = jpaQueryFactory.selectFrom(qMatch).where(qMatch.chatId.eq(chatId)).orderBy(qMatch.swiperId.desc());
        if (writeLock) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        return query.fetch();
    }

}
