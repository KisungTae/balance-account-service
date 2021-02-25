package com.beeswork.balanceaccountservice.dao.accountquestion;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AccountQuestionDAO extends BaseDAO<AccountQuestion> {
    long findAllByAnswer(UUID accountId, Map<Integer, Boolean> answers);
    List<Question> findAllQuestionsSelected(UUID accountId);

}
