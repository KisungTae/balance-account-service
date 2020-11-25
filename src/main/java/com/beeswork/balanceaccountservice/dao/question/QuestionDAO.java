package com.beeswork.balanceaccountservice.dao.question;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.question.Question;

import java.util.List;

public interface QuestionDAO extends BaseDAO<Question> {

    Question findById(Integer questionId);
    List<Question> findAllByIds(List<Integer> ids);
    Question findNthNotIn(List<Integer> questionIds, int offset);
    long count();
    List<Question> findAllWithLimitAndOffset(int limit, int offset);
}
