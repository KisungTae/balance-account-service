package com.beeswork.balanceaccountservice.dao.login;


import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.entity.login.LoginId;
import com.beeswork.balanceaccountservice.entity.login.QLogin;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class LoginDAOImpl extends BaseDAOImpl<Login> implements LoginDAO {

    private final QLogin qLogin = QLogin.login;

    @Autowired
    public LoginDAOImpl(EntityManager entityManager,
                        JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Login findById(LoginId loginId) {
        return jpaQueryFactory.selectFrom(qLogin).where(qLogin.loginId.eq(loginId)).fetchFirst();
    }

    @Override
    public Login findByAccountId(UUID accountId) {
        return jpaQueryFactory.selectFrom(qLogin)
                              .where(qLogin.account.id.eq(accountId))
                              .fetchFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaQueryFactory.selectFrom(qLogin).where(qLogin.email.eq(email)).fetchCount() > 0;
    }
}
