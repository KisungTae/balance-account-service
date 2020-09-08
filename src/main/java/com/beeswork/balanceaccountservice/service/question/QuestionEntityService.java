package com.beeswork.balanceaccountservice.service.question;

import com.beeswork.balanceaccountservice.entity.Question;

import java.util.HashMap;
import java.util.List;

public interface QuestionEntityService {

    List<Question> findAllByIds(List<Long> ids);
}
