package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ListSwipesDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.*;

public interface SwipeService {

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    List<QuestionDTO> swipe(UUID accountId, UUID identityToken, UUID swipedId);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    ClickDTO click(UUID accountId, UUID identityToken, UUID swipedId, Map<Integer, Boolean> answers);

    ListSwipesDTO listSwipes(UUID accountId, UUID identityToken, Date fetchedAt);
    List<SwipeDTO> listClicks(UUID accountId, UUID identityToken, Date fetchedAt);
}
