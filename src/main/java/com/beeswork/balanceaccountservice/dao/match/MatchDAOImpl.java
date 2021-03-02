package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.match.QMatchDTO;
import com.beeswork.balanceaccountservice.entity.account.QAccount;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.QChat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.match.QMatch;;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
    public Match findById(UUID matcherId, UUID matchedId) {
        return entityManager.find(Match.class, new MatchId(matcherId, matchedId));
    }

    @Override
    public List<MatchDTO> findAllAfter(UUID matcherId, Date matchFetchedAt) {
        Expression<Date> updatedAtCase = new CaseBuilder().when(qMatch.updatedAt.after(qAccount.updatedAt))
                                                          .then(qMatch.updatedAt)
                                                          .otherwise(qAccount.updatedAt);

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(qMatch.matcher.id.eq(matcherId));
        condition.and(qMatch.updatedAt.after(matchFetchedAt).or(qAccount.updatedAt.after(matchFetchedAt)));
        condition.and(qMatch.deleted.eq(false));

        return jpaQueryFactory.select(new QMatchDTO(qMatch.chat.id,
                                                    qMatch.matched.id,
                                                    qMatch.unmatched,
                                                    qAccount.name,
                                                    qAccount.repPhotoKey,
                                                    qAccount.deleted,
                                                    qMatch.active,
                                                    qMatch.createdAt,
                                                    updatedAtCase))
                              .from(qMatch)
                              .leftJoin(qAccount).on(qAccount.id.eq(qMatch.matched.id))
                              .where(condition)
                              .fetch();
    }

}
