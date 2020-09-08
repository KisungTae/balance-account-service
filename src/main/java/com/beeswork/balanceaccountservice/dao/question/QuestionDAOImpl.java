package com.beeswork.balanceaccountservice.dao.question;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.QQuestion;
import com.beeswork.balanceaccountservice.entity.Question;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QuestionDAOImpl extends BaseDAOImpl<Question> implements QuestionDAO {

    private final QQuestion qQuestion = QQuestion.question;

    @Autowired
    public QuestionDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    public List<Question> findAllByIds(List<Long> ids) {
        return jpaQueryFactory.selectFrom(qQuestion).where(qQuestion.id.in(ids)).fetch();
    }


}
