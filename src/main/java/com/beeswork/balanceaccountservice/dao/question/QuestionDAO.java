package com.beeswork.balanceaccountservice.dao.question;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.Question;

import java.util.List;

public interface QuestionDAO extends BaseDAO<Question> {

    List<Question> findAllByIds(List<Long> ids);
}
