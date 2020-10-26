package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.account.AccountQuestionDTO;
import com.beeswork.balanceaccountservice.dto.account.CardDTO;

import java.util.Date;
import java.util.List;

public interface AccountService {
    void save(AccountDTO accountDTO);
    void saveProfile(String accountId, String email, String name, Date birth, String about, boolean gender);
    void saveLocation(String accountId, String email, double latitude, double longitude);
    void saveFCMToken(String accountId, String email, String token);
    void saveQuestions(String accountId, String email, List<AccountQuestionDTO> accountQuestionDTOs);
    List<CardDTO> recommend(String accountId, String email, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude);
}