package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ProfileService {
    //    delay = 0 then default to 1 second
    //    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    ProfileDTO getProfile(UUID accountId);

    CardDTO getCard(UUID accountId, UUID swipedId);

    void saveProfile(UUID accountId,
                     String name,
                     LocalDate birthDate,
                     String about,
                     int height,
                     boolean gender,
                     double latitude,
                     double longitude);

    void saveBio(UUID accountId, String about, Integer height);

    void saveLocation(UUID accountId, double latitude, double longitude, Date updatedAt);

    List<CardDTO> recommend(UUID accountId, int distance, int minAge, int maxAge, boolean gender, int pageIndex);

    void saveEmail(UUID accountId, String email);

    String getEmail(UUID accountId);
}
