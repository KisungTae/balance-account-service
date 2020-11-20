package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.question.QuestionSetChangedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SwipeServiceImpl extends BaseServiceImpl implements SwipeService {

    private static final int SWIPE_POINT = 50;

    private final AccountDAO accountDAO;
    private final SwipeDAO   swipeDAO;
    private final ChatDAO chatDAO;

    @Autowired
    public SwipeServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, SwipeDAO swipeDAO,
                            ChatDAO chatDAO) {

        super(modelMapper);
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.chatDAO = chatDAO;
    }

    @Override
    @Transactional
    public BalanceGameDTO swipe(String accountId, String identityToken, Long swipeId, String swipedId) {

        Account swiper = accountDAO.findBy(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(swiper);

        Account swiped = accountDAO.findWithQuestions(UUID.fromString(swipedId));
        checkIfSwipedValid(swiped);

        if (swipeDAO.existsByClicked(swiper.getId(), swiped.getId(), true))
            throw new SwipeClickedExistsException();

        if (swiper.getPoint() < SWIPE_POINT)
            throw new AccountShortOfPointException();

        BalanceGameDTO balanceGameDTO = new BalanceGameDTO();
        balanceGameDTO.setSwipeId(swipeId);

        if (swipeId == null) {
            Swipe swipe = new Swipe(swiper, swiped, false, new Date(), new Date());
            swipeDAO.persist(swipe);
            balanceGameDTO.setSwipeId(swipe.getId());
        }
        
        for (AccountQuestion accountQuestion : swiped.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            balanceGameDTO.getQuestions().add(new QuestionDTO(question.getId(),
                                                              question.getDescription(),
                                                              question.getTopOption(),
                                                              question.getBottomOption()));
        }

        return balanceGameDTO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ClickedProjection> listClicked(String accountId, String identityToken, Date fetchedAt) {

        UUID accountUUId = UUID.fromString(accountId);
        Account account = accountDAO.findBy(accountUUId, UUID.fromString(identityToken));
        checkIfAccountValid(account);
        return swipeDAO.findAllClickedAfter(accountUUId, fetchedAt);
    }

    @Override
    @Transactional
    public ClickDTO click(Long swipeId, String accountId, String identityToken, String swipedId, Map<Integer, Boolean> answers) {

        UUID swiperUUId = UUID.fromString(accountId);
        UUID swipedUUId = UUID.fromString(swipedId);

        Swipe swipe = swipeDAO.findWithAccounts(swipeId, swiperUUId, swipedUUId);

        Account swiper = swipe.getSwiper();
        Account swiped = swipe.getSwiped();

        checkIfAccountValid(swiper, UUID.fromString(identityToken));
        checkIfSwipedValid(swiped);

        if (swiper.getPoint() < SWIPE_POINT)
            throw new AccountShortOfPointException();

        int point = swiper.getPoint();
        point -= SWIPE_POINT;
        swiper.setPoint(point);

        ClickDTO clickDTO = new ClickDTO();

        for (AccountQuestion accountQuestion : swiped.getAccountQuestions()) {
            Boolean answer = answers.get(accountQuestion.getQuestionId());
            if (answer == null) throw new QuestionSetChangedException();
            else if (accountQuestion.isAnswer() != answer) return clickDTO;
        }

        swipe.setClicked(true);
        swipe.setUpdatedAt(new Date());

        // match
        if (swipeDAO.existsByClicked(swipedUUId, swiperUUId, true)) {

            Chat chat = new Chat();
            Date date = new Date();

            chatDAO.persist(chat);

            swiper.getMatches().add(new Match(swiper, swiped, chat, false, date, date));
            swiped.getMatches().add(new Match(swiped, swiper, chat, false, date, date));

            clickDTO.setupAsMatch(swiped.getId(), swiped.getName(), swiped.getRepPhotoKey(),
                                  chat.getId(), swiped.getFcmToken(), false);
        } else {
            clickDTO.setupAsClicked(swiped.getId(), swiped.getRepPhotoKey(), swiped.getFcmToken(), swipe.getUpdatedAt());
        }

//        throw new BadRequestException();
//        throw new SwipedNotFoundException();
        return clickDTO;
    }
}
