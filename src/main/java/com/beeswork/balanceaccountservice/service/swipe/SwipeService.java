package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;

import java.util.Date;
import java.util.List;

public interface SwipeService {

    List<QuestionDTO> swipe(String swiperId, String swiperEmail, String swipedId);
    List<ClickedProjection> listClicked(String swipedId, String email, Date fetchedAt);

}
