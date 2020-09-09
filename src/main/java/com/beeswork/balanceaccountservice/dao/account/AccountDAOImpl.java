package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.QAccount;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class AccountDAOImpl extends BaseDAOImpl<Account> implements AccountDAO {

    private final QAccount qAccount = QAccount.account;

    @Autowired
    public AccountDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Account findById(UUID id) throws AccountNotFoundException {
        Account account = jpaQueryFactory.selectFrom(qAccount).where(qAccount.id.eq(id)).fetchOne();
        if (account == null) throw new AccountNotFoundException();
        return account;
    }

    @Override
    public List<Account> findAllByLocation(UUID accountId, int distance, int minAge, int maxAge, boolean showMe, int index)
    throws AccountNotFoundException {

        Account account = findById(accountId);

        



        return null;
    }


}
