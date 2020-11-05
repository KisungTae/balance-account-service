package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.account.AccountQuestionDTO;
import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.account.ProfileDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    ProfileDTO getProfile(String accountId, String email);
    List<QuestionDTO> getQuestions(String accountId, String email);
    void saveProfile(String accountId, String email, String name, Date birth, String about, Integer height, boolean gender);
    void saveLocation(String accountId, String email, double latitude, double longitude);
    void saveFCMToken(String accountId, String email, String token);
    void saveQuestions(String accountId, String email, List<AccountQuestionDTO> accountQuestionDTOs);
    List<CardDTO> recommend(String accountId, String email, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude);
}