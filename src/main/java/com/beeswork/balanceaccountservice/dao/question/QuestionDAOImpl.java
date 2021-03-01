package com.beeswork.balanceaccountservice.dao.question;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;

import com.beeswork.balanceaccountservice.entity.account.QAccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.QQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class QuestionDAOImpl extends BaseDAOImpl<Question> implements QuestionDAO {

    private final QQuestion qQuestion = QQuestion.question;
    private final QAccountQuestion qAccountQuestion = QAccountQuestion.accountQuestion;

    @Autowired
    public QuestionDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Question findById(Integer questionId) {
        return jpaQueryFactory.selectFrom(qQuestion).where(qQuestion.id.eq(questionId)).fetchOne();
    }

    @Override
    public List<Question> findAllIn(Set<Integer> ids) {
        return jpaQueryFactory.selectFrom(qQuestion).where(qQuestion.id.in(ids)).fetch();
    }

    @Override
    public Question findNthNotIn(List<Integer> questionIds, int offset) {
        return jpaQueryFactory.selectFrom(qQuestion)
                              .where(qQuestion.id.notIn(questionIds))
                              .offset(offset)
                              .limit(1)
                              .fetchOne();
    }

    @Override
    public long count() {
        return jpaQueryFactory.selectFrom(qQuestion).fetchCount();
    }

    @Override
    public List<Question> findAll(int limit, int offset) {
        return jpaQueryFactory.selectFrom(qQuestion).limit(limit).offset(offset).fetch();
    }




}
