package com.beeswork.balanceaccountservice.repository;

import com.beeswork.balanceaccountservice.entity.AccountType;
import com.beeswork.balanceaccountservice.entity.QAccountType;
import com.beeswork.balanceaccountservice.repository.base.BaseRepository;
import com.beeswork.balanceaccountservice.repository.base.BaseRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class AccountTypeRepositoryImpl extends BaseRepositoryImpl<AccountType> implements AccountTypeRepository {

    @Autowired
    public AccountTypeRepositoryImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
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
