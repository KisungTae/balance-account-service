package com.beeswork.balanceaccountservice.service.question;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface QuestionService {
    QuestionDTO randomQuestion(List<Integer> questionIds);
    List<QuestionDTO> listRandomQuestions();
    void saveQuestionAnswers(UUID accountId, Map<Integer, Boolean> answers);
    List<QuestionDTO> listQuestions(UUID accountId);
}
