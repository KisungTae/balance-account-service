package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.account.QAccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class AccountQuestionDAOImpl extends BaseDAOImpl<AccountQuestion> implements AccountQuestionDAO {

    private final QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;
    private final QQuestion qQuestion = QQuestion.question;

    @Autowired
    public AccountQuestionDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public long findAllByAnswers(UUID accountId, Map<Integer, Boolean> answers) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (Map.Entry<Integer, Boolean> entry : answers.entrySet()) {
            booleanBuilder.or(qAccountQuestion.questionId.eq(entry.getKey())
                                                         .and(qAccountQuestion.answer.eq(entry.getValue())));
        }
        return jpaQueryFactory.selectFrom(qAccountQuestion)
                              .where(qAccountQuestion.accountId.eq(accountId).and(booleanBuilder))
                              .fetchCount();
    }

    @Override
    public List<Question> findAllQuestionsSelected(UUID accountId) {
        return jpaQueryFactory.selectFrom(qQuestion)
                              .leftJoin(qAccountQuestion).on(qQuestion.id.eq(qAccountQuestion.questionId))
                              .where(qAccountQuestion.accountId.eq(accountId).and(qAccountQuestion.selected.eq(true)))
                              .fetch();
    }


}