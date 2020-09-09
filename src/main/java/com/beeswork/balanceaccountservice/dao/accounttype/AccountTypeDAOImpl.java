package com.beeswork.balanceaccountservice.dao.accounttype;

import com.beeswork.balanceaccountservice.entity.account.AccountType;
import com.beeswork.balanceaccountservice.entity.QAccountType;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class AccountTypeDAOImpl extends BaseDAOImpl<AccountType> implements AccountTypeDAO {

    @Autowired
    public AccountTypeDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Transactional
    public void test() {

        AccountType accountType = new AccountType();
        accountType.setDescription("페이스북 로그인222323232");

        entityManager.persist(accountType);


        List<AccountType> accountTypes = jpaQueryFactory.selectFrom(QAccountType.accountType)
                                                        .where(QAccountType.accountType.description.like("%페이스%"))
                                                        .fetch();

        System.out.println("called");
    }
}
