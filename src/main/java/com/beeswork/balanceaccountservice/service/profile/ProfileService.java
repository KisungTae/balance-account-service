package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.account.PreRecommendDTO;
import com.beeswork.balanceaccountservice.dto.account.ProfileDTO;
import org.locationtech.jts.geom.Point;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ProfileService {

    ProfileDTO getProfile(UUID accountId, UUID identityToken);

    // delay = 0 then default to 1 second
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    void saveProfile(UUID accountId,
                     UUID identityToken,
                     String name,
                     Date birth,
                     String about,
                     int height,
                     boolean gender);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    void saveAbout(UUID accountId, UUID identityToken, String about, Integer height);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    void saveLocation(UUID accountId, UUID identityToken, double latitude, double longitude, Date updatedAt);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    PreRecommendDTO preRecommend(UUID accountId,
                                 UUID identityToken,
                                 Double latitude,
                                 Double longitude,
                                 Date locationUpdatedAt,
                                 boolean reset);

    List<CardDTO> recommend(int distance, int minAge, int maxAge, boolean gender, Point location, int pageIndex);


}
