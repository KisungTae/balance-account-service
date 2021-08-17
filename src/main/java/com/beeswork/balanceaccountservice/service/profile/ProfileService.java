package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.PreRecommendDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;
import com.beeswork.balanceaccountservice.dto.profile.RecommendDTO;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import org.locationtech.jts.geom.Point;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ProfileService {

    ProfileDTO getProfile(UUID accountId, UUID identityToken);

    CardDTO getCard(UUID accountId, UUID identityToken, UUID swipedId);

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

    RecommendDTO recommend(UUID accountId, UUID identityToken, int distance, int minAge, int maxAge, boolean gender, int pageIndex);

    void saveEmail(UUID accountId, UUID identityToken, String email);
    String getEmail(UUID accountId, UUID identityToken);
}
