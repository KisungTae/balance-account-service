package com.beeswork.balanceaccountservice.service.question;
import com.beeswork.balanceaccountservice.dto.question.ListQuestionsDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface QuestionService {
    QuestionDTO randomQuestion(List<Integer> questionIds);
    void saveQuestionAnswers(UUID accountId, Map<Integer, Boolean> answers);
    ListQuestionsDTO listQuestions(UUID accountId);
}
