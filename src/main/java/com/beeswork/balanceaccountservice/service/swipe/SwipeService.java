package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SwipeService {

    BalanceGameDTO swipe(String swiperId, String swiperEmail, Long swipeId, String swipedId);
    List<ClickedProjection> listClicked(String swipedId, String email, Date fetchedAt);
    ClickDTO click(Long swipeId, String swiperId, String swiperEmail, String swipedId, Map<Long, Boolean> answers);
}
