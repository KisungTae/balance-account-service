package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.dto.swipe.CountClicksDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.*;

public interface SwipeService {

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    List<QuestionDTO> swipe(UUID swiperId, UUID swipedId);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    ClickDTO click(UUID swiperId, UUID swipedId, Map<Integer, Boolean> answers);

    List<SwipeDTO> listClicks(UUID accountId, int startPosition, int loadSize);

    List<SwipeDTO> fetchClicks(UUID accountId, UUID lastSwiperId, int loadSize);

    CountClicksDTO countClicks(UUID accountId);
}
