package com.beeswork.balanceaccountservice.service.question;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;

import java.util.List;

public interface QuestionService {

    QuestionDTO randomQuestion(List<Long> currentQuestionIds);
}
