package com.beeswork.balanceaccountservice.dao.account;


import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.PushToken;
import com.beeswork.balanceaccountservice.entity.account.PushTokenId;
import com.beeswork.balanceaccountservice.entity.account.QPushToken;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class PushTokenDAOImpl extends BaseDAOImpl<PushToken> implements PushTokenDAO {

    private final QPushToken qPushToken = QPushToken.pushToken;

    @Autowired
    public PushTokenDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public PushToken findById(PushTokenId pushTokenId) {
        return jpaQueryFactory.selectFrom(qPushToken).where(qPushToken.pushTokenId.eq(pushTokenId)).fetchOne();
    }
}
