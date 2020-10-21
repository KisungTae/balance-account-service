package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.swipe.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.service.account.AccountInterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class SwipeServiceImpl implements SwipeService, SwipeInterService {

    private final AccountInterService accountInterService;
    private final SwipeDAO swipeDAO;

    @Autowired
    public SwipeServiceImpl(AccountInterService accountInterService, SwipeDAO swipeDAO) {
        this.accountInterService = accountInterService;
        this.swipeDAO = swipeDAO;
    }

    @Override
    @Transactional
    public BalanceGameDTO swipe(SwipeDTO swipeDTO)
    throws AccountNotFoundException, AccountInvalidException, SwipeClickedExistsException,
           AccountShortOfPointException, AccountBlockedException {

        UUID swiperUUId = UUID.fromString(swipeDTO.getSwiperId());
        UUID swipedUUId = UUID.fromString(swipeDTO.getSwipedId());

        Account swiper = accountInterService.findValid(swiperUUId, swipeDTO.getSwiperEmail());

        if (existsByClicked(swiperUUId, swipedUUId, true))
            throw new SwipeClickedExistsException();

        int currentPoint = swiper.getPoint();

//        if (currentPoint < AppConstant.SWIPE_POINT)
//            throw new AccountShortOfPointException();

        Account swiped = accountInterService.findWithQuestions(swipedUUId);

        Swipe swipe = new Swipe(swiper, swiped, false, new Date(), new Date());
        swiper.getSwipes().add(swipe);

        currentPoint -= AppConstant.SWIPE_POINT;
        swiper.setPoint(currentPoint);

        BalanceGameDTO balanceGameDTO = new BalanceGameDTO();
        balanceGameDTO.setSwipeId(swipe.getId());

        for (AccountQuestion accountQuestion : swiped.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            QuestionDTO questionDTO = new QuestionDTO(question.getDescription(),
                                                      question.getTopOption(),
                                                      question.getBottomOption(),
                                                      accountQuestion.isSelected());
            balanceGameDTO.getQuestions().add(questionDTO);
        }

        return balanceGameDTO;
    }

    @Override
    public boolean existsByClicked(UUID swiperId, UUID swipedId, boolean clicked) {
        return swipeDAO.countByClicked(swiperId, swipedId, true) > 0;
    }
}
