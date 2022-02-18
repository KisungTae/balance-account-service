package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MatchService {
    List<MatchDTO> fetchMatches(UUID swiperId, UUID lastSwipedId, int loadSize, MatchPageFilter matchPageFilter);
    ListMatchesDTO listMatches(UUID accountId, Date fetchedAt);
    void unmatch(UUID swiperId, UUID swipedId);
    void reportMatch(UUID reporterId, UUID reportedId, int reportReasonId, String description);

}
