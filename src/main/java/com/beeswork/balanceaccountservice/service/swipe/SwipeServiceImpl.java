package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.accountquestion.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeId;
import com.beeswork.balanceaccountservice.exception.account.AccountQuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.projection.ClickProjection;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SwipeServiceImpl extends BaseServiceImpl implements SwipeService {

    private static final int SWIPE_POINT      = 200;
    private static final int FREE_SWIPE_A_DAY = 2000;

    private final AccountDAO         accountDAO;
    private final SwipeDAO           swipeDAO;
    private final ChatDAO            chatDAO;
    private final FirebaseService    firebaseService;
    private final AccountQuestionDAO accountQuestionDAO;

    @Autowired
    public SwipeServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            SwipeDAO swipeDAO,
                            ChatDAO chatDAO,
                            FirebaseService firebaseService,
                            AccountQuestionDAO accountQuestionDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.chatDAO = chatDAO;
        this.firebaseService = firebaseService;
        this.accountQuestionDAO = accountQuestionDAO;
    }

    @Override
    @Transactional
    public List<QuestionDTO> swipe(UUID accountId, UUID identityToken, UUID swipedId) {
        Account swiper = accountDAO.findById(accountId);
        checkIfAccountValid(swiper, identityToken);

        Account swiped = accountDAO.findWithAccountQuestions(swipedId);
        checkIfSwipedValid(swiped);

        if (swiped.getAccountQuestions().size() == 0)
            throw new AccountQuestionNotFoundException();

        rechargeFreeSwipe(swiper);
        if (swiper.getFreeSwipe() < SWIPE_POINT && swiper.getPoint() < SWIPE_POINT)
            throw new AccountShortOfPointException();

        Swipe swipe = swipeDAO.findById(new SwipeId(accountId, swipedId));
        if (swipe != null && swipe.isClicked())
            throw new SwipeClickExistsException();

        Date updatedAt = new Date();
        if (swipe == null)
            swipe = new Swipe(swiper, swiped, updatedAt);
        swipe.setUpdatedAt(updatedAt);
        swipe.setCount((swipe.getCount() + 1));
        swipeDAO.persist(swipe);

        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (AccountQuestion accountQuestion : swiped.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            questionDTOs.add(new QuestionDTO(question.getId(),
                                             question.getDescription(),
                                             question.getTopOption(),
                                             question.getBottomOption()));
        }
        return questionDTOs;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ClickProjection> listClick(UUID accountId, UUID identityToken, Date fetchedAt) {
        Account account = accountDAO.findById(accountId);
        checkIfAccountValid(account, identityToken);
        return swipeDAO.findAllClickAfter(accountId, offsetFetchedAt(fetchedAt));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ClickedProjection> listClicked(UUID accountId, UUID identityToken, Date fetchedAt) {
        Account account = accountDAO.findById(accountId);
        checkIfAccountValid(account, identityToken);
        return swipeDAO.findAllClickedAfter(accountId, offsetFetchedAt(fetchedAt));
    }

    @Override
    @Transactional
    public ClickDTO click(UUID accountId, UUID identityToken, UUID swipedId, Map<Integer, Boolean> answers) {
        Swipe subSwipe = swipeDAO.findById(new SwipeId(accountId, swipedId));

        if (subSwipe == null)
            throw new SwipeNotFoundException();
        if (subSwipe.isClicked())
            throw new SwipeClickExistsException();

        Account swiper = subSwipe.getSwiper();
        Account swiped = subSwipe.getSwiped();

        checkIfAccountValid(swiper, identityToken);
        checkIfSwipedValid(swiped);

        if (swiper.getFreeSwipe() >= SWIPE_POINT)
            swiper.setFreeSwipe((swiper.getFreeSwipe() - SWIPE_POINT));
        else if (swiper.getPoint() < SWIPE_POINT)
            throw new AccountShortOfPointException();
        else swiper.setPoint((swiper.getPoint() - SWIPE_POINT));

        ClickDTO clickDTO = new ClickDTO();
        if (accountQuestionDAO.findAllByAnswer(swipedId, answers) != answers.size())
            return clickDTO;

        subSwipe.setClicked(true);
        Date updatedAt = new Date();
        subSwipe.setUpdatedAt(updatedAt);

        Swipe objSwipe = swipeDAO.findById(new SwipeId(swipedId, accountId));
        if (objSwipe == null) {
            clickDTO.setupAsClick(swiped.getId(), updatedAt);
        } else if (objSwipe.isClicked()) {
            Chat chat = new Chat();
            chatDAO.persist(chat);

            swiper.getMatches().add(new Match(swiper, swiped, chat, false, updatedAt, updatedAt));
            swiped.getMatches().add(new Match(swiped, swiper, chat, false, updatedAt, updatedAt));

            subSwipe.setMatched(true);
            objSwipe.setMatched(true);
            clickDTO.setupAsMatch(chat.getId(), swiped.getId(), swiped.getName(), swiped.getRepPhotoKey(), updatedAt);
        } else {
            // for the case where two users click at the same time
            objSwipe.setVersion((objSwipe.getVersion() + 1));
            clickDTO.setupAsClick(swiped.getId(), updatedAt);
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
