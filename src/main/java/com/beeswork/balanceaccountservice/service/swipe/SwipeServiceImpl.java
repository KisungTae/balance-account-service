package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.accountquestion.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.firebase.ClickedNotificationDTO;
import com.beeswork.balanceaccountservice.dto.firebase.FirebaseNotification;
import com.beeswork.balanceaccountservice.dto.firebase.MatchedNotificationDTO;
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
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.projection.ClickProjection;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import org.apache.commons.lang3.time.DateUtils;
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

        Swipe swipe = swipeDAO.findBy(accountId, swipedId);

        if (swipe != null && swipe.isClicked())
            throw new SwipeClickedExistsException();

        if (swipe == null) {
            Date today = new Date();
            swipe = new Swipe(swiper, swiped, false, 0, today, today);
        }

        if (swipe.getCount() != 0)
            swipe.setUpdatedAt(new Date());

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
    public ClickDTO click(UUID accountId,
                          UUID identityToken,
                          UUID swipedId,
                          Map<Integer, Boolean> answers,
                          Locale locale) throws InterruptedException {

//      TODO: findWithAccounts null check
//      TODO: two thread access at the same time (if we lock swipe, then second thread will wait for swipe so no dead lock?)
//      TODO: df


        System.out.println(Thread.currentThread().getName() + " starts click()");

        Swipe swipe = swipeDAO.findById(new SwipeId(accountId, swipedId));
        int version = swipe.getVersion() + 1;
        swipe.setVersion(version);

        Thread.sleep(5000);
//        System.out.println("swipe account name: " + swipe.getSwiper().getName());


//        Swipe swipe = swipeDAO.findWithAccounts(accountId, swipedId);
//
//        Account swiper = swipe.getSwiper();
//        Account swiped = swipe.getSwiped();
//
//        checkIfAccountValid(swiper, identityToken);
//        checkIfSwipedValid(swiped);
//
//        rechargeFreeSwipe(swiper);
//
//        if (swiper.getFreeSwipe() >= SWIPE_POINT)
//            swiper.setFreeSwipe((swiper.getFreeSwipe() - SWIPE_POINT));
//        else if (swiper.getPoint() < SWIPE_POINT)
//            throw new AccountShortOfPointException();
//        else swiper.setPoint((swiper.getPoint() - SWIPE_POINT));
//
//        ClickDTO clickDTO = new ClickDTO();
//
//        if (accountQuestionDAO.findAllByAnswer(swipedId, answers) != answers.size())
//            return clickDTO;
//
//        swipe.setClicked(true);
//        Date updatedAt = new Date();
//        swipe.setUpdatedAt(updatedAt);



        // match
//        if (swipeDAO.existsByClicked(swipedId, accountId, true)) {
//            Chat chat = new Chat();
//            chatDAO.persist(chat);
//
//            swiper.getMatches().add(new Match(swiper, swiped, chat, false, updatedAt, updatedAt));
//            swiped.getMatches().add(new Match(swiped, swiper, chat, false, updatedAt, updatedAt));

//            clickDTO.setupAsMatch(chat.getId(), swiped.getId(), swiped.getName(), swiped.getRepPhotoKey(), updatedAt);
//            firebaseService.sendNotification(new MatchedNotificationDTO(swiped.getFcmToken(),
//                                                                        swiped.getName(),
//                                                                        swiped.getRepPhotoKey()),
//                                             locale);
//        } else {
//            clickDTO.setupAsClick(swiped.getId(), updatedAt);
//            firebaseService.sendNotification(new ClickedNotificationDTO(swiped.getFcmToken(),
//                                                                        swiped.getName(),
//                                                                        swiped.getRepPhotoKey()),
//                                             locale);
//        }
//        return clickDTO;
        return null;
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
