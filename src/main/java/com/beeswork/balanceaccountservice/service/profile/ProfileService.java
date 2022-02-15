package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;
import com.beeswork.balanceaccountservice.dto.profile.RecommendDTO;

import java.util.Date;
import java.util.UUID;

public interface ProfileService {
    //    delay = 0 then default to 1 second
    //    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    ProfileDTO getProfile(UUID accountId);
    CardDTO getCard(UUID accountId, UUID swipedId);
    void saveProfile(UUID accountId, String name, Date birth, String about, int height, boolean gender, double latitude, double longitude);
    void saveAbout(UUID accountId, String about, Integer height);
    void saveLocation(UUID accountId, double latitude, double longitude, Date updatedAt);
    RecommendDTO recommend(UUID accountId, int distance, int minAge, int maxAge, boolean gender, int pageIndex);
    void saveEmail(UUID accountId, String email);
    String getEmail(UUID accountId);
}
