package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.*;

import java.util.*;

public interface SwipeService {

//    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    List<QuestionDTO> like(UUID swiperId, UUID swipedId, Locale locale);

//    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    ClickDTO click(UUID swiperId, UUID swipedId, Map<Integer, Boolean> answers, Locale locale);

    List<SwipeDTO> listSwipes(UUID accountId, int startPosition, int loadSize);

    List<SwipeDTO> fetchSwipes(UUID accountId, UUID lastSwiperId, int loadSize);

    CountSwipesDTO countSwipes(UUID accountId);
}
