package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.firebase.AbstractFirebaseNotification;
import com.beeswork.balanceaccountservice.dto.firebase.FirebaseNotification;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.projection.ClickProjection;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.*;

public interface SwipeService {

    List<ClickedProjection> listClicked(UUID accountId, UUID identityToken, Date fetchedAt);
    List<ClickProjection> listClick(UUID accountId, UUID identityToken, Date fetchedAt);
    List<QuestionDTO> swipe(UUID accountId, UUID identityToken, UUID swipedId);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    ClickDTO click(UUID accountId, UUID identityToken, UUID swipedId, Map<Integer, Boolean> answers, Locale locale);
}
