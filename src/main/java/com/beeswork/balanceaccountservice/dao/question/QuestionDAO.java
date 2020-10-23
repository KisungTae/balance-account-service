package com.beeswork.balanceaccountservice.dao.question;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.question.Question;

import java.util.List;

public interface QuestionDAO extends BaseDAO<Question> {

    Question findById(long questionId);
    List<Question> findAllByIds(List<Long> ids);
    Question findNthNotIn(List<Long> questionIds, int offset);
    long count();
}
