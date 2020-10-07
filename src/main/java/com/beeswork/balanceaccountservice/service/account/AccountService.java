package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;

import java.util.List;

public interface AccountService {

    void save(AccountDTO accountDTO) throws AccountNotFoundException, QuestionNotFoundException;

    void saveProfile(AccountDTO accountDTO) throws AccountNotFoundException;
    void saveQuestions(AccountQuestionSaveDTO accountQuestionSaveDTO)
    throws AccountNotFoundException, QuestionNotFoundException;
    void saveLocation(LocationDTO locationDTO) throws AccountNotFoundException;

    void saveFirebaseMessagingToken(FirebaseMessagingTokenDTO firebaseMessagingTokenDTO)
    throws AccountNotFoundException;
}
