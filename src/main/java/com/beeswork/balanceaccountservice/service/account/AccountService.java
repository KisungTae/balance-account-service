package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.dto.account.*;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import org.locationtech.jts.geom.Point;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AccountService {

    ProfileDTO getProfile(UUID accountId, UUID identityToken);

    // delay = 0 then default to 1 second
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1))
    void saveProfile(UUID accountId, UUID identityToken, String name, Date birth, String about, Integer height,
                     boolean gender);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1))
    void saveAbout(UUID accountId, UUID identityToken, String about, Integer height);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1))
    void saveLocation(UUID accountId, UUID identityToken, double latitude, double longitude,
                      Date updatedAt);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1))
    void saveFCMToken(UUID accountId, UUID identityToken, String token);

    void saveAnswers(UUID accountId, UUID identityToken, Map<Integer, Boolean> answers);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1))
    void saveEmail(UUID accountId, UUID identityToken, String email);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1))
    PreRecommendDTO preRecommend(UUID accountId, UUID identityToken, Double latitude, Double longitude,
                                 Date locationUpdatedAt, boolean reset);

    List<CardDTO> recommend(int distance, int minAge, int maxAge, boolean gender, Point location, int index);

    void deleteAccount(UUID accountId, UUID identityToken);
}