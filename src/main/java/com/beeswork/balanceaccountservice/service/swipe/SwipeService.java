package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.ListQuestionsDTO;
import com.beeswork.balanceaccountservice.dto.swipe.*;

import java.util.*;

public interface SwipeService {

    ListQuestionsDTO like(UUID swiperId, UUID swipedId, Locale locale);

    ClickDTO click(UUID swiperId, UUID swipedId, Map<Integer, Boolean> answers, Locale locale);

    List<SwipeDTO> fetchSwipes(UUID swipedId, Long loadKey, int loadSize, boolean isAppend, boolean isIncludeLoadKey);
}
