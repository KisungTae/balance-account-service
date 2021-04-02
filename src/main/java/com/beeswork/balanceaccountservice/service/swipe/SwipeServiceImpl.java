package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ListClickedDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeId;
import com.beeswork.balanceaccountservice.exception.account.AccountQuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeMatchedExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
        Account swiper = validateAccount(accountDAO.findById(accountId), identityToken);
        Account swiped = validateSwiped(accountDAO.findById(swipedId));

        rechargeFreeSwipe(swiper);
        if (swiper.getFreeSwipe() < SWIPE_POINT && swiper.getPoint() < SWIPE_POINT)
            throw new AccountShortOfPointException();

        List<Question> questions = accountQuestionDAO.findAllQuestionsSelected(swipedId);
        if (questions == null || questions.size() <= 0)
            throw new AccountQuestionNotFoundException();

        Swipe swipe = swipeDAO.findById(new SwipeId(accountId, swipedId), true);
        Date updatedAt = new Date();
        if (swipe == null) swipe = new Swipe(swiper, swiped, updatedAt);
        else if (swipe.isMatched()) throw new SwipeMatchedExistsException();
        else if (swipe.isClicked()) throw new SwipeClickedExistsException();

        swipe.setCount((swipe.getCount() + 1));
        swipeDAO.persist(swipe);
        return modelMapper.map(questions, new TypeToken<List<QuestionDTO>>() {
        }.getType());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListClickedDTO listClicked(UUID accountId, UUID identityToken, Date fetchedAt) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        ListClickedDTO listClickedDTO = new ListClickedDTO(swipeDAO.findAllClickedAfter(accountId, fetchedAt), fetchedAt);
        if (listClickedDTO.getSwipeDTOs() == null) return listClickedDTO;

        for (SwipeDTO swipeDTO : listClickedDTO.getSwipeDTOs()) {
            Date updatedAt = swipeDTO.getUpdatedAt();
            if (updatedAt != null && updatedAt.after(listClickedDTO.getFetchedAt()))
                listClickedDTO.setFetchedAt(swipeDTO.getUpdatedAt());
            swipeDTO.setUpdatedAt(null);
        }
        return listClickedDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<SwipeDTO> listClickers(UUID accountId, UUID identityToken, Date fetchedAt) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        return swipeDAO.findAllClickersAfter(accountId, fetchedAt);
    }

    @Override
    @Transactional
    public ClickDTO click(UUID accountId, UUID identityToken, UUID swipedId, Map<Integer, Boolean> answers) {
        Swipe subSwipe, objSwipe;
        if (accountId.compareTo(swipedId) > 0) {
            subSwipe = swipeDAO.findById(new SwipeId(accountId, swipedId), true);
            objSwipe = swipeDAO.findById(new SwipeId(swipedId, accountId), true);
        } else {
            objSwipe = swipeDAO.findById(new SwipeId(swipedId, accountId), true);
            subSwipe = swipeDAO.findById(new SwipeId(accountId, swipedId), true);
        }

        if (subSwipe == null) throw new SwipeNotFoundException();
        else if (subSwipe.isClicked()) throw new SwipeClickedExistsException();
        else if (subSwipe.isMatched()) throw new SwipeMatchedExistsException();

        Account swiper = validateAccount(subSwipe.getSwiper(), identityToken);
        Account swiped = validateSwiped(subSwipe.getSwiped());

        if (swiper.getFreeSwipe() >= SWIPE_POINT)
            swiper.setFreeSwipe((swiper.getFreeSwipe() - SWIPE_POINT));
        else if (swiper.getPoint() < SWIPE_POINT)
            throw new AccountShortOfPointException();
        else swiper.setPoint((swiper.getPoint() - SWIPE_POINT));

        ClickDTO clickDTO = new ClickDTO();
        if (accountQuestionDAO.countAllByAnswers(swipedId, answers) != answers.size()) {
            clickDTO.setupAsMissed();
            return clickDTO;
        }

        Date updatedAt = new Date();
        subSwipe.setClicked(true);
        subSwipe.setUpdatedAt(updatedAt);

        if (objSwipe == null || !objSwipe.isClicked())
            clickDTO.setupAsClicked(swiper.getId(), swiper.getRepPhotoKey(), swiped.getId());
        else {
            Chat chat = new Chat();
            chatDAO.persist(chat);

            swiper.getMatches().add(new Match(swiper, swiped, chat, updatedAt));
            swiped.getMatches().add(new Match(swiped, swiper, chat, updatedAt));

            subSwipe.setMatched(true);
            objSwipe.setMatched(true);
            clickDTO.setupAsMatched(chat.getId(),
                                    swiped.getId(),
                                    swiped.getName(),
                                    swiped.getRepPhotoKey(),
                                    swiper.getId(),
                                    swiper.getName(),
                                    swiper.getRepPhotoKey(),
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
