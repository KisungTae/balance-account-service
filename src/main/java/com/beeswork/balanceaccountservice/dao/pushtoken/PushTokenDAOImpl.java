package com.beeswork.balanceaccountservice.dao.pushtoken;


import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushTokenId;
import com.beeswork.balanceaccountservice.entity.pushtoken.QPushToken;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class PushTokenDAOImpl extends BaseDAOImpl<PushToken> implements PushTokenDAO {

    private final QPushToken qPushToken = QPushToken.pushToken;

    @Autowired
    public PushTokenDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public PushToken findById(PushTokenId pushTokenId) {
        return jpaQueryFactory.selectFrom(qPushToken).where(qPushToken.pushTokenId.eq(pushTokenId)).fetchFirst();
    }

    @Override
    public PushToken findRecentByAccountId(UUID accountId) {
        return jpaQueryFactory.selectFrom(qPushToken)
                              .where(qPushToken.account.id.eq(accountId))
                              .orderBy(qPushToken.updatedAt.desc())
                              .limit(1)
                              .fetchFirst();
    }

    @Override
    public List<PushToken> findAllBy(String token, PushTokenType pushTokenType) {
        return jpaQueryFactory.selectFrom(qPushToken)
                              .where(qPushToken.token.eq(token).and(qPushToken.pushTokenId.type.eq(pushTokenType)))
                              .fetch();
    }
}
