package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.firebase.FirebaseTokenDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;

public interface AccountService {

    void save(AccountDTO accountDTO) throws AccountNotFoundException, QuestionNotFoundException;

    void saveProfile(AccountDTO accountDTO) throws AccountNotFoundException;
    void saveQuestions(AccountQuestionSaveDTO accountQuestionSaveDTO)
    throws AccountNotFoundException, QuestionNotFoundException;
    void saveLocation(LocationDTO locationDTO) throws AccountNotFoundException;

    void saveFirebaseToken(FirebaseTokenDTO firebaseTokenDTO)
    throws AccountNotFoundException;
}
