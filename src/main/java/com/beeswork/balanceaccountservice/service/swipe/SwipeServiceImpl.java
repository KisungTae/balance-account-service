package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.match.BalanceDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeListDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeBalancedExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SwipeServiceImpl implements SwipeService {

    private final AccountDAO accountDAO;
    private final SwipeDAO swipeDAO;

    @Autowired
    public SwipeServiceImpl(AccountDAO accountDAO, SwipeDAO swipeDAO) {
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
    }

    @Override
    @Transactional
    public SwipeListDTO listSwipes(String accountId) {

        SwipeListDTO swipeListDTO = new SwipeListDTO();

        for (Swipe swipe : swipeDAO.findAllSwiped(UUID.fromString(accountId))) {
            swipeListDTO.getSwiperIds().add(swipe.getSwiperId().toString());
        }

        return swipeListDTO;
    }

    @Override
    @Transactional
    public BalanceDTO swipe(SwipeDTO swipeDTO)
    throws AccountNotFoundException, AccountInvalidException, SwipeBalancedExistsException,
           AccountShortOfPointException {

        UUID swiperUUId = UUID.fromString(swipeDTO.getSwiperId());
        UUID swipedUUId = UUID.fromString(swipeDTO.getSwipedId());

        Account swiper = accountDAO.findById(swiperUUId);

//        if (!swiper.getEmail().equals(swipeDTO.getSwiperEmail()))
//            throw new AccountInvalidException();

        if (swipeDAO.balancedExists(swiperUUId, swipedUUId))
            throw new SwipeBalancedExistsException();

        int currentPoint = swiper.getPoint();

//        if (currentPoint < AppConstant.SWIPE_POINT)
//            throw new AccountShortOfPointException();

        Account swiped = accountDAO.findByIdWithQuestions(swipedUUId);

        Swipe swipe = new Swipe(swiper, swiped, false, new Date(), new Date());
        swiper.getSwipes().add(swipe);

        currentPoint -= AppConstant.SWIPE_POINT;
        swiper.setPoint(currentPoint);

        accountDAO.persist(swiper);

        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setSwipeId(swipe.getId());
        balanceDTO.setSwipedId(swiped.getId().toString());

        for (AccountQuestion accountQuestion : swiped.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            QuestionDTO questionDTO = new QuestionDTO(question.getDescription(),
                                                      question.getTopOption(),
                                                      question.getBottomOption(),
                                                      accountQuestion.isSelected(),
                                                      accountQuestion.getSequence());
            balanceDTO.getQuestionDTOs().add(questionDTO);
        }

        return balanceDTO;
    }
}
