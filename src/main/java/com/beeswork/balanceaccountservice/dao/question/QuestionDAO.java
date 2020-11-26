package com.beeswork.balanceaccountservice.dao.question;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.question.Question;

import java.util.List;
import java.util.Set;

public interface QuestionDAO extends BaseDAO<Question> {

    Question findById(Integer questionId);
    List<Question> findAllByIds(Set<Integer> ids);
    Question findNthNotIn(List<Integer> questionIds, int offset);
    long count();
    List<Question> findAllWithLimitAndOffset(int limit, int offset);
}
