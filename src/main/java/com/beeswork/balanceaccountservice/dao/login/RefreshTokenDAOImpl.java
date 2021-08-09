package com.beeswork.balanceaccountservice.dao.login;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.login.QRefreshToken;
import com.beeswork.balanceaccountservice.entity.login.RefreshToken;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class RefreshTokenDAOImpl extends BaseDAOImpl<RefreshToken> implements RefreshTokenDAO {

    private final QRefreshToken qRefreshToken = QRefreshToken.refreshToken;

    @Autowired
    public RefreshTokenDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public RefreshToken findByAccountId(UUID accountId) {
        return jpaQueryFactory.selectFrom(qRefreshToken).where(qRefreshToken.accountId.eq(accountId)).fetchFirst();
    }
}
