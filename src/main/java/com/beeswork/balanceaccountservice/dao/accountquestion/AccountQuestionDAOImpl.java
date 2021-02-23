package com.beeswork.balanceaccountservice.dao.accountquestion;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.account.QAccountQuestion;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class AccountQuestionDAOImpl extends BaseDAOImpl<AccountQuestion> implements AccountQuestionDAO {

    QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;

    @Autowired
    public AccountQuestionDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public long findAllByAnswer(UUID accountId, Map<Integer, Boolean> answers) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (Map.Entry<Integer, Boolean> entry : answers.entrySet()) {
            booleanBuilder.or(qAccountQuestion.questionId.eq(entry.getKey())
                                                         .and(qAccountQuestion.answer.eq(entry.getValue())));
        }
        return jpaQueryFactory.selectFrom(qAccountQuestion)
                              .where(qAccountQuestion.accountId.eq(accountId).and(booleanBuilder))
                              .fetchCount();
    }

}
