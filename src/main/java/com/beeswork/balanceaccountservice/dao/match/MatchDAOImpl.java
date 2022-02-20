package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.match.QMatchDTO;
import com.beeswork.balanceaccountservice.dto.swipe.QSwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.chat.QChat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.match.QMatch;;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class MatchDAOImpl extends BaseDAOImpl<Match> implements MatchDAO {

    private final QMatch   qMatch   = QMatch.match;
    private final QAccount qAccount = QAccount.account;
    private final QChat    qChat    = QChat.chat;

    @Autowired
    public MatchDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public List<MatchDTO> findAllBy(UUID swiperId, UUID lastSwipedId, int loadSize, MatchPageFilter matchPageFilter) {
        BooleanExpression condition = matchPageCondition(swiperId, matchPageFilter);
        if (lastSwipedId != null) {
            JPQLQuery<Long> lastMatchId = JPAExpressions.select(qMatch.id)
                                                        .from(qMatch)
                                                        .where(qMatch.swiper.id.eq(swiperId).and(qMatch.swiped.id.eq(lastSwipedId)));
            condition = condition.and(qMatch.id.lt(lastMatchId));
        }
        return jpaQueryFactory.select(new QMatchDTO(qMatch.id,
                                                    qMatch.chatId,
                                                    qMatch.swiper.id,
                                                    qMatch.swiped.id,
                                                    qMatch.unmatched,
                                                    qMatch.lastReadChatMessageId,
                                                    qMatch.lastChatMessageId,
                                                    qMatch.lastChatMessageBody,
                                                    qAccount.name,
                                                    qAccount.profilePhotoKey,
                                                    qAccount.deleted))
                              .from(qMatch)
                              .leftJoin(qAccount).on(qAccount.id.eq(qMatch.swiped.id))
                              .where(condition)
                              .orderBy(qMatch.id.desc())
                              .limit(loadSize)
                              .fetch();
    }

    @Override
    public List<MatchDTO> findAllBy(UUID swiperId, int startPosition, int loadSize, MatchPageFilter matchPageFilter) {
        return jpaQueryFactory.select(new QMatchDTO(qMatch.id,
                                                    qMatch.chatId,
                                                    qMatch.swiper.id,
                                                    qMatch.swiped.id,
                                                    qMatch.unmatched,
                                                    qMatch.lastReadChatMessageId,
                                                    qMatch.lastChatMessageId,
                                                    qMatch.lastChatMessageBody,
                                                    qAccount.name,
                                                    qAccount.profilePhotoKey,
                                                    qAccount.deleted))
                              .from(qMatch)
                              .leftJoin(qAccount).on(qAccount.id.eq(qMatch.swiped.id))
                              .where(matchPageCondition(swiperId, matchPageFilter))
                              .orderBy(qMatch.id.desc())
                              .limit(loadSize)
                              .offset(startPosition).fetch();
    }

    private BooleanExpression matchPageCondition(UUID swiperId, MatchPageFilter matchPageFilter) {
        BooleanExpression condition = qMatch.swiper.id.eq(swiperId).and(qMatch.deleted.eq(false));
        if (matchPageFilter != null) {
            switch (matchPageFilter) {
                case CHAT:
                    condition = condition.and(qMatch.lastChatMessageId.isNotNull());
                    break;
                case MATCH:
                    condition = condition.and(qMatch.lastChatMessageId.isNull());
                    break;
                case CHAT_WITH_UNREAD_MESSAGE:
                    condition = condition.and(qMatch.lastReadChatMessageId.lt(qMatch.lastChatMessageId));
                    break;
            }
        }
        return condition;
    }

    @Override
    public Match findBy(UUID swiperId, UUID swipedId, boolean writeLock) {
        return entityManager.find(Match.class, new MatchId(swiperId, swipedId), writeLock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
    }

}
