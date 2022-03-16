package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.*;

import java.util.*;

public interface SwipeService {

    List<QuestionDTO> like(UUID swiperId, UUID swipedId, Locale locale);

    ClickDTO click(UUID swiperId, UUID swipedId, Map<Integer, Boolean> answers, Locale locale);

    ListSwipesDTO listSwipes(final UUID swipedId, final int startPosition, final int loadSize);

    ListSwipesDTO fetchSwipes(final UUID swipedId, final Long lastSwipeId, final int loadSize);
}
