package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import org.locationtech.jts.geom.Point;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AccountService {
    void saveQuestionAnswers(UUID accountId, Map<Integer, Boolean> answers);
    List<QuestionDTO> listQuestions(UUID accountId);
    DeleteAccountDTO deleteAccount(UUID accountId);
}