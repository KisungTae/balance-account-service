package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.UUID;

public interface MatchService {
    ListMatchesDTO listMatches(UUID accountId, Date fetchedAt);
    void unmatch(UUID swiperId, UUID swipedId);

}
