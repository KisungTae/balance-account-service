package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.AccountDTO;
import com.beeswork.balanceaccountservice.entity.Account;
import com.beeswork.balanceaccountservice.entity.QAccount;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;

@Repository
public class AccountDAOImpl extends BaseDAOImpl<Account> implements AccountDAO {

    private final QAccount qAccount = QAccount.account;

    @Autowired
    public AccountDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    public Account findByIdAndName(long id, String email) {
        return jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(id).and(qAccount.email.eq(email))).fetchOne();
    }

    public Account findById(long id) {
        return jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(id)).fetchOne();
    }

    public void save(Account account) {
        entityManager.persist(account);
    }


}
