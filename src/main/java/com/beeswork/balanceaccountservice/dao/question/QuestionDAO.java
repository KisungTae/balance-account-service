package com.beeswork.balanceaccountservice.dao.question;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.question.Question;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface QuestionDAO extends BaseDAO<Question> {

    List<Question> findAllIn(Set<Integer> ids);
    Question findNthNotIn(List<Integer> questionIds, int offset);
    long count();
    List<Question> findAll(int limit, int offset);
}
