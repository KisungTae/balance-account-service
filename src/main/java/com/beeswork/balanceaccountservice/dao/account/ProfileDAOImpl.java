package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Profile;
import com.beeswork.balanceaccountservice.entity.account.QProfile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class ProfileDAOImpl extends BaseDAOImpl<Profile> implements ProfileDAO {

    private final QProfile qProfile = QProfile.profile;

    @Autowired
    public ProfileDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Profile findById(UUID accountId) {
        return jpaQueryFactory.selectFrom(qProfile).where(qProfile.accountId.eq(accountId)).fetchOne();
    }
}
