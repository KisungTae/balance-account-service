package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SwipeService {

    BalanceGameDTO swipe(String accountId, String identityToken, Long swipeId, String swipedId);
    List<ClickedProjection> listClicked(String accountId, String identityToken, Date fetchedAt);
    ClickDTO click(Long swipeId, String accountId, String identityToken, String swipedId, Map<Long, Boolean> answers);
}
