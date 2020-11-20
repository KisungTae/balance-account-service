package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SwipeService {

    List<ClickedProjection> listClicked(String accountId, String identityToken, Date fetchedAt);
    BalanceGameDTO swipe(String accountId, String identityToken, Long swipeId, String swipedId);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    ClickDTO click(Long swipeId, String accountId, String identityToken, String swipedId, Map<Integer, Boolean> answers);
}
