package com.beeswork.balanceaccountservice.service.question;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;

import java.util.List;

public interface QuestionService {

    QuestionDTO refreshQuestion(String accountId, String email, List<Long> currentQuestionIds);
}
