package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.account.AccountQuestionDTO;
import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.account.ProfileDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AccountService {
    ProfileDTO getProfile(String accountId, String identityToken);
    List<QuestionDTO> getQuestions(String accountId, String identityToken);
    void saveProfile(String accountId, String identityToken, String name, Date birth, String about, Integer height, boolean gender);
    void saveAbout(String accountId, String identityToken, String about, Integer height);
    void saveLocation(String accountId, String identityToken, double latitude, double longitude);
    void saveFCMToken(String accountId, String identityToken, String token);
    void saveAnswers(String accountId, String identityToken, Map<Long, Boolean> answers);
    List<CardDTO> recommend(String accountId, String identityToken, int distance, int minAge, int maxAge, boolean gender, int index);
}