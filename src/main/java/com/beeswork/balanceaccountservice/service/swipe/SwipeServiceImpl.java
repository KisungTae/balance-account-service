package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.accountquestion.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ListSwipesDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
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
import com.beeswork.balanceaccountservice.exception.swipe.SwipeMatchedExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.projection.ClickProjection;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
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
    public SwipeServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            SwipeDAO swipeDAO,
                            ChatDAO chatDAO,
                            AccountQuestionDAO accountQuestionDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.chatDAO = chatDAO;
        this.accountQuestionDAO = accountQuestionDAO;
    }

    @Override
    @Transactional
    public List<QuestionDTO> like(UUID accountId, UUID identityToken, UUID swipedId) {
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
        Date updatedAt = new Date();
        if (swipe == null) swipe = new Swipe(swiper, swiped, updatedAt);
        else if (swipe.isMatched()) throw new SwipeMatchedExistsException();
        else if (swipe.isClicked()) throw new SwipeClickExistsException();

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
    public ListSwipesDTO listSwipes(UUID accountId, UUID identityToken, boolean clicked, Date fetchedAt) {
        checkIfAccountValid(accountDAO.findById(accountId), identityToken);
        ListSwipesDTO listSwipesDTO = new ListSwipesDTO(fetchedAt);
        if (clicked) listSwipesDTO.setSwipeDTOs(swipeDAO.findAllClickedAfter(accountId, fetchedAt));
        else listSwipesDTO.setSwipeDTOs(swipeDAO.findAllClickAfter(accountId, fetchedAt));
        for (SwipeDTO swipeDTO : listSwipesDTO.getSwipeDTOs()) {
            Date updatedAt = swipeDTO.getUpdatedAt();
            if (updatedAt != null && updatedAt.after(listSwipesDTO.getFetchedAt()))
                listSwipesDTO.setFetchedAt(swipeDTO.getUpdatedAt());
            swipeDTO.setUpdatedAt(null);
        }
        return listSwipesDTO;
    }

    @Override
    @Transactional
    public ClickDTO click(UUID accountId, UUID identityToken, UUID swipedId, Map<Integer, Boolean> answers) {

        boolean priorityOverSwiper = accountId.compareTo(swipedId) > 0;
        System.out.println(Thread.currentThread().getName() +
                           " starts click() with accountId: " +
                           accountId +
                           " and priorityOverSwiper: " +
                           priorityOverSwiper);

        Swipe subSwipe = swipeDAO.findById(new SwipeId(accountId, swipedId));

        if (subSwipe == null)
            throw new SwipeNotFoundException();
        if (subSwipe.isClicked())
            throw new SwipeClickExistsException();
        if (subSwipe.isMatched())
            throw new SwipeMatchedExistsException();

//        Account swiper = subSwipe.getSwiper();
//        Account swiped = subSwipe.getSwiped();

//        checkIfAccountValid(swiper, identityToken);
//        checkIfSwipedValid(swiped);

//        if (swiper.getFreeSwipe() >= SWIPE_POINT)
//            swiper.setFreeSwipe((swiper.getFreeSwipe() - SWIPE_POINT));
//        else if (swiper.getPoint() < SWIPE_POINT)
//            throw new AccountShortOfPointException();
//        else swiper.setPoint((swiper.getPoint() - SWIPE_POINT));

        ClickDTO clickDTO = new ClickDTO();
//        if (accountQuestionDAO.findAllByAnswer(swipedId, answers) != answers.size())
//            return clickDTO;

        Date updatedAt = new Date();


        Swipe objSwipe = swipeDAO.findById(new SwipeId(swipedId, accountId));
        if (objSwipe == null) {
//            clickDTO.setupAsClick(swiped.getId(), updatedAt);
        } else if (objSwipe.isClicked()) {
            System.out.println("click(): matched");
//            Chat chat = new Chat();
//            chatDAO.persist(chat);

//            swiper.getMatches().add(new Match(swiper, swiped, chat, false, updatedAt, updatedAt));
//            swiped.getMatches().add(new Match(swiped, swiper, chat, false, updatedAt, updatedAt));
            if (priorityOverSwiper) {
                subSwipe.setClicked(true);
                subSwipe.setUpdatedAt(updatedAt);
                subSwipe.setMatched(true);
                objSwipe.setMatched(true);
            } else {
                objSwipe.setMatched(true);
                subSwipe.setClicked(true);
                subSwipe.setUpdatedAt(updatedAt);
                subSwipe.setMatched(true);
            }


//            clickDTO.setupAsMatch(chat.getId(), swiped.getId(), swiped.getName(), swiped.getRepPhotoKey(), updatedAt);
        } else {
            System.out.println("click(): not matched");
            if (priorityOverSwiper) {
                subSwipe.setClicked(true);
                subSwipe.setUpdatedAt(updatedAt);
                objSwipe.setVersion((objSwipe.getVersion() + 1));
            } else {
                objSwipe.setVersion((objSwipe.getVersion() + 1));
                subSwipe.setClicked(true);
                subSwipe.setUpdatedAt(updatedAt);
            }
            // for the case where two users click at the same time
//            objSwipe.setVersion((objSwipe.getVersion() + 1));
//            clickDTO.setupAsClick(swiped.getId(), updatedAt);
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
