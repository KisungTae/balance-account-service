package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.firebase.FCMTokenDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;

import java.util.Date;
import java.util.List;

public interface AccountService {

    void save(AccountDTO accountDTO);

    void saveProfile(String accountId, String email, String name, Date birth, String about, boolean gender);

    void saveLocation(String accountId, String email, double latitude, double longitude);
    void saveFCMToken(String accountId, String email, String token);
    void saveQuestions(String accountId, String email, List<AccountQuestionDTO> accountQuestionDTOs);
}
