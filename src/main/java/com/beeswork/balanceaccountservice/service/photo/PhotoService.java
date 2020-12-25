package com.beeswork.balanceaccountservice.service.photo;

import com.beeswork.balanceaccountservice.dto.account.PhotoDTO;
import com.beeswork.balanceaccountservice.dto.photo.GenerateS3PreSignedURLDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;
import java.util.Map;

public interface PhotoService {
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    void addPhoto(String accountId, String identityToken, String photoKey, int sequence);

    List<PhotoDTO> listPhotos(String accountId, String identityToken);
    void deletePhoto(String accountId, String identityToken, String photoKey);
    void reorderPhotos(String accountId, String identityToken, Map<String, Integer> photoOrders);
}
