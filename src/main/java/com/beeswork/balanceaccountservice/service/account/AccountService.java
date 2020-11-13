package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import org.locationtech.jts.geom.Point;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AccountService {
    ProfileDTO getProfile(String accountId, String identityToken);

    List<QuestionDTO> getQuestions(String accountId, String identityToken);

    List<PhotoDTO> getPhotos(String accountId, String identityToken);

    void saveProfile(String accountId, String identityToken, String name, String email, Date birth, String about, Integer height,
                     boolean gender);

    void saveAbout(String accountId, String identityToken, String about, Integer height);

    void saveLocation(String accountId, String identityToken, double latitude, double longitude,
                      Date locationUpdatedAt);

    void saveFCMToken(String accountId, String identityToken, String token);

    void saveAnswers(String accountId, String identityToken, Map<Long, Boolean> answers);

    PreRecommendDTO preRecommend(String accountId, String identityToken, Double latitude, Double longitude,
                                 Date locationUpdatedAt, boolean reset);

    List<CardDTO> recommend(int distance, int minAge, int maxAge, boolean gender, Point location, int index);
}