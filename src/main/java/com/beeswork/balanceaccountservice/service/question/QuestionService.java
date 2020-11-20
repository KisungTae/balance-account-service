package com.beeswork.balanceaccountservice.service.question;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;

import java.util.List;

public interface QuestionService {

    List<QuestionDTO> getQuestions(String accountId, String identityToken);
    QuestionDTO randomQuestion(List<Integer> currentQuestionIds);
}
