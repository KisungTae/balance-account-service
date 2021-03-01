package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.question.QQuestionDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
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
import java.util.Set;
import java.util.UUID;

@Repository
public class AccountQuestionDAOImpl extends BaseDAOImpl<AccountQuestion> implements AccountQuestionDAO {

    private final QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;
    private final QQuestion        qQuestion        = QQuestion.question;

    @Autowired
    public AccountQuestionDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    public List<AccountQuestion> findAllIn(UUID accountId, Set<Integer> questionIds) {
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(qAccountQuestion.accountQuestionId.accountId.eq(accountId));
        condition.and(qAccountQuestion.selected.eq(true).or(qAccountQuestion.accountQuestionId.questionId.in(questionIds)));
        return jpaQueryFactory.selectFrom(qAccountQuestion).where(condition).fetch();
    }

    @Override
    public long countAllByAnswers(UUID accountId, Map<Integer, Boolean> answers) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (Map.Entry<Integer, Boolean> entry : answers.entrySet()) {
            booleanBuilder.or(qAccountQuestion.accountQuestionId.questionId.eq(entry.getKey())
                                                                           .and(qAccountQuestion.answer.eq(entry.getValue())));
        }
        return jpaQueryFactory.selectFrom(qAccountQuestion)
                              .where(qAccountQuestion.accountQuestionId.accountId.eq(accountId).and(booleanBuilder))
                              .fetchCount();
    }

    @Override
    public List<Question> findAllQuestionsSelected(UUID accountId) {
        return jpaQueryFactory.selectFrom(qQuestion)
                              .leftJoin(qAccountQuestion)
                              .on(qQuestion.id.eq(qAccountQuestion.accountQuestionId.questionId))
                              .where(conditionForSelected(accountId))
                              .fetch();
    }

    @Override
    public List<QuestionDTO> findAllQuestionDTOsWithAnswer(UUID accountId) {
        return jpaQueryFactory.select(new QQuestionDTO(qQuestion.id,
                                                       qQuestion.description,
                                                       qQuestion.topOption,
                                                       qQuestion.bottomOption,
                                                       qAccountQuestion.answer))
                              .from(qAccountQuestion)
                              .leftJoin(qQuestion)
                              .where(conditionForSelected(accountId))
                              .fetch();
    }

    private BooleanBuilder conditionForSelected(UUID accountId) {
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(qAccountQuestion.accountQuestionId.accountId.eq(accountId));
        condition.and(qAccountQuestion.selected.eq(true));
        return condition;
    }


}
