package com.beeswork.balanceaccountservice.service.photo;

import com.beeswork.balanceaccountservice.dto.photo.PhotoDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PhotoService {
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    void savePhoto(UUID accountId, String photoKey, int sequence);

    List<PhotoDTO> listPhotos(UUID accountId);
    void deletePhoto(UUID accountId, String photoKey);
    void reorderPhotos(UUID accountId, Map<String, Integer> photoOrders);
}
