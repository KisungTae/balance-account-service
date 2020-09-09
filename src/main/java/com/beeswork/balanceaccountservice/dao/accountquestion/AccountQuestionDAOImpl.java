package com.beeswork.balanceaccountservice.dao.accountquestion;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.QAccountQuestion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class AccountQuestionDAOImpl extends BaseDAOImpl<AccountQuestion> implements AccountQuestionDAO {

    QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;

    @Autowired
    public AccountQuestionDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

}
