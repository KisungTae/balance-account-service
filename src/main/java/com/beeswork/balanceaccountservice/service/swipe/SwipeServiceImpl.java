package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.accountquestion.AccountQuestionDAO;
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
import com.beeswork.balanceaccountservice.exception.account.AccountQuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.question.QuestionSetChangedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.projection.ClickProjection;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import io.micrometer.core.instrument.util.TimeUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SwipeServiceImpl extends BaseServiceImpl implements SwipeService {

    private static final int SWIPE_POINT = 200;
    private static final int FREE_SWIPE_A_DAY = 2000;

    private final AccountDAO accountDAO;
    private final SwipeDAO swipeDAO;
    private final ChatDAO chatDAO;
    private final AccountQuestionDAO accountQuestionDAO;

    @Autowired
    public SwipeServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, SwipeDAO swipeDAO,
                            ChatDAO chatDAO, AccountQuestionDAO accountQuestionDAO) {

        super(modelMapper);
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.chatDAO = chatDAO;
        this.accountQuestionDAO = accountQuestionDAO;
    }

    @Override
    @Transactional
    public BalanceGameDTO swipe(String accountId, String identityToken, Long swipeId, String swipedId) {

        Account swiper = accountDAO.findBy(UUID.fromString(accountId), UUID.fromString(identityToken));
        checkIfAccountValid(swiper);

        Account swiped = accountDAO.findWithAccountQuestions(UUID.fromString(swipedId));
        checkIfSwipedValid(swiped);

        if (swiped.getAccountQuestions().size() == 0)
            throw new AccountQuestionNotFoundException();

        if (swipeDAO.existsByClicked(swiper.getId(), swiped.getId(), true))
            throw new SwipeClickedExistsException();

        rechargeFreeSwipe(swiper);
        if (swiper.getFreeSwipe() < SWIPE_POINT && swiper.getPoint() < SWIPE_POINT)
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
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<ClickProjection> listClick(String accountId, String identityToken, Date fetchedAt) {

        UUID accountUUId = UUID.fromString(accountId);
        Account account = accountDAO.findBy(accountUUId, UUID.fromString(identityToken));
        checkIfAccountValid(account);
        return swipeDAO.findAllClickAfter(accountUUId, fetchedAt);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
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

        rechargeFreeSwipe(swiper);

        if (swiper.getFreeSwipe() >= SWIPE_POINT)
            swiper.setFreeSwipe((swiper.getFreeSwipe() - SWIPE_POINT));
        else if (swiper.getPoint() < SWIPE_POINT)
            throw new AccountShortOfPointException();
        else swiper.setPoint((swiper.getPoint() - SWIPE_POINT));

        ClickDTO clickDTO = new ClickDTO();

        if (accountQuestionDAO.findAllByAnswer(swipedUUId, answers) != answers.size())
            return clickDTO;

        swipe.setClicked(true);

        Date updatedAt = new Date();
        swipe.setUpdatedAt(updatedAt);

        // match
        if (swipeDAO.existsByClicked(swipedUUId, swiperUUId, true)) {

            Chat chat = new Chat();
            chatDAO.persist(chat);

            swiper.getMatches().add(new Match(swiper, swiped, chat, false, updatedAt, updatedAt));
            swiped.getMatches().add(new Match(swiped, swiper, chat, false, updatedAt, updatedAt));

            clickDTO.setupAsMatch(swiper.getName(),
                                  swiper.getRepPhotoKey(),
                                  chat.getId(),
                                  swiped.getId(),
                                  swiped.getName(),
                                  swiped.getRepPhotoKey(),
                                  swiped.getFcmToken(),
                                  updatedAt);
        } else {
            clickDTO.setupAsClick(swiper.getName(),
                                  swiper.getRepPhotoKey(),
                                  swiped.getId(),
                                  swiped.getFcmToken(),
                                  updatedAt);
        }

        return clickDTO;
    }

    private void rechargeFreeSwipe(Account swiper) {

        Date today = new Date();
        long elapsedTime = today.getTime() - swiper.getFreeSwipeUpdatedAt().getTime();
        long rechargePeriod = 24 * 60 * 60 * 1000;
        if (elapsedTime > rechargePeriod) {
            swiper.setFreeSwipe(FREE_SWIPE_A_DAY);
            swiper.setFreeSwipeUpdatedAt(today);
        }
    }
}
