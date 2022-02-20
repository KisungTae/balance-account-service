package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.*;

import java.util.*;

public interface SwipeService {

    List<QuestionDTO> like(UUID swiperId, UUID swipedId, Locale locale);

    ClickDTO click(UUID swiperId, UUID swipedId, Map<Integer, Boolean> answers, Locale locale);

    ListSwipesDTO listSwipes(final UUID accountId, final int startPosition, final int loadSize);

    List<SwipeDTO> fetchSwipes(UUID accountId, UUID lastSwiperId, int loadSize);

    CountSwipesDTO countSwipes(UUID accountId);
}
