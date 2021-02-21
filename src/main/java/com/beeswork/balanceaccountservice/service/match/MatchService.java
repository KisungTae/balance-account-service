package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dto.match.ListMatchDTO;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MatchService {
    ListMatchDTO listMatches(UUID accountId, UUID identityToken, Date matchFetchedAt);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    void unmatch(UUID accountId, UUID identityToken, UUID unmatchedId);
}
