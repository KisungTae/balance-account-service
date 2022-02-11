package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.constant.ClickResult;
import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeMetaDAO;
import com.beeswork.balanceaccountservice.dao.wallet.WalletDAO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.*;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.Wallet;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeMeta;
import com.beeswork.balanceaccountservice.exception.account.AccountQuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.*;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.PersistenceException;
import java.util.*;

@Service
public class SwipeServiceImpl extends BaseServiceImpl implements SwipeService {

    private final AccountDAO                 accountDAO;
    private final SwipeDAO                   swipeDAO;
    private final ChatDAO                    chatDAO;
    private final AccountQuestionDAO         accountQuestionDAO;
    private final ProfileDAO                 profileDAO;
    private final SwipeMetaDAO               swipeMetaDAO;
    private final WalletDAO                  walletDAO;
    private final ModelMapper                modelMapper;
    private final StompService               stompService;
    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public SwipeServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            SwipeDAO swipeDAO,
                            ChatDAO chatDAO,
                            AccountQuestionDAO accountQuestionDAO,
                            ProfileDAO profileDAO, SwipeMetaDAO swipeMetaDAO,
                            WalletDAO walletDAO,
                            StompService stompService,
                            PlatformTransactionManager transactionManager) {
        this.modelMapper = modelMapper;
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.chatDAO = chatDAO;
        this.accountQuestionDAO = accountQuestionDAO;
        this.profileDAO = profileDAO;
        this.swipeMetaDAO = swipeMetaDAO;
        this.walletDAO = walletDAO;
        this.stompService = stompService;
        this.transactionManager = transactionManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public List<QuestionDTO> like(UUID swiperId, UUID swipedId, Locale locale) {
        LikeTransactionResult result = transactionTemplate.execute(status -> {
            Account swiper = accountDAO.findById(swiperId);
            Account swiped = accountDAO.findById(swipedId);
            validateSwiped(swiped);
            Wallet wallet = walletDAO.findByAccountId(swiper.getId(), true);
            SwipeMeta swipeMeta = swipeMetaDAO.findFirst();
            rechargeFreeSwipe(wallet, swipeMeta);

            if (wallet.getFreeSwipe() < swipeMeta.getSwipePoint() && wallet.getPoint() < swipeMeta.getSwipePoint()) {
                throw new AccountShortOfPointException();
            }

            List<Question> questions = accountQuestionDAO.findAllQuestionsSelected(swipedId);
            if (questions == null || questions.size() <= 0) {
                throw new AccountQuestionNotFoundException();
            }
            Swipe swipe = swipeDAO.findBy(swiperId, swipedId, true);
            Date updatedAt = new Date();
            if (swipe == null) {
                swipe = new Swipe(swiper, swiped, updatedAt);
                Profile profile = profileDAO.findById(swiped.getId(), true);
                if (profile != null) {
                    profile.incrementScore();
                }
                swipeDAO.persist(swipe);
            } else if (swipe.isMatched()) {
                throw new SwipeMatchedExistsException();
            } else if (swipe.isClicked()) {
                throw new SwipeClickedExistsException();
            }
            swipe.setCount((swipe.getCount() + 1));
            return new LikeTransactionResult(questions, swipe);
        });

        if (result == null) {
            throw new PersistenceException();
        }

        SwipeDTO swipeDTO = modelMapper.map(result.getSwipe(), SwipeDTO.class);
        stompService.push(swipeDTO, locale);
        return modelMapper.map(result.getQuestions(), new TypeToken<List<QuestionDTO>>() {}.getType());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListClicksDTO listClicks(UUID accountId, int startPosition, int loadSize) {
        List<SwipeDTO> clicks = swipeDAO.findClicks(accountId, startPosition, loadSize);
        ListClicksDTO listClicksDTO = new ListClicksDTO();
        for (SwipeDTO click : clicks) {
            if (click.getDeleted()) {
                listClicksDTO.getDeletedSwiperIds().add(click.getSwiperId());
            } else {
                listClicksDTO.getClickDTOs().add(click);
            }
        }
        return listClicksDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<SwipeDTO> fetchClicks(UUID accountId, UUID lastSwiperId, int loadSize) {
        return swipeDAO.findClicks(accountId, lastSwiperId, loadSize);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CountClicksDTO countClicks(UUID accountId) {
        return new CountClicksDTO(swipeDAO.countClicks(accountId));
    }

    @Override
    public ClickDTO click(UUID swiperId, UUID swipedId, Map<Integer, Boolean> answers, Locale locale) {
        ClickTransactionResult result = transactionTemplate.execute(status -> {
            Swipe subSwipe, objSwipe;
            if (swiperId.compareTo(swipedId) > 0) {
                subSwipe = swipeDAO.findBy(swiperId, swipedId, true);
                objSwipe = swipeDAO.findBy(swipedId, swiperId, true);
            } else {
                objSwipe = swipeDAO.findBy(swipedId, swiperId, true);
                subSwipe = swipeDAO.findBy(swiperId, swipedId, true);
            }

            if (subSwipe == null) {
                throw new SwipeNotFoundException();
            } else if (subSwipe.isClicked()) {
                throw new SwipeClickedExistsException();
            } else if (subSwipe.isMatched()) {
                throw new SwipeMatchedExistsException();
            }

            Account swiper = subSwipe.getSwiper();
            Account swiped = subSwipe.getSwiped();
            validateSwiped(swiped);

            Wallet wallet = walletDAO.findByAccountId(swiper.getId(), true);
            SwipeMeta swipeMeta = swipeMetaDAO.findFirst();

            if (wallet.getFreeSwipe() >= swipeMeta.getSwipePoint()) {
                wallet.setFreeSwipe((wallet.getFreeSwipe() - swipeMeta.getSwipePoint()));
            } else if (wallet.getPoint() < swipeMeta.getSwipePoint()) {
                throw new AccountShortOfPointException();
            } else {
                wallet.setPoint((wallet.getPoint() - swipeMeta.getSwipePoint()));
            }

            if (accountQuestionDAO.countAllByAnswers(swipedId, answers) != answers.size()) {
                return new ClickTransactionResult(ClickResult.MISSED, null, null);
            }

            Date now = new Date();
            subSwipe.setClicked(true);
            subSwipe.setUpdatedAt(now);

            if (objSwipe == null || !objSwipe.isClicked()) {
                return new ClickTransactionResult(ClickResult.CLICKED, subSwipe, null);
            } else {
                Chat chat = new Chat();
                chatDAO.persist(chat);

                Match subMatch = new Match(swiper, swiped, chat, now);
                Match objMatch = new Match(swiped, swiper, chat, now);
                swiper.getMatches().add(subMatch);
                swiped.getMatches().add(objMatch);

                subSwipe.setMatched(true);
                objSwipe.setMatched(true);
                objSwipe.setUpdatedAt(now);

                return new ClickTransactionResult(ClickResult.MATCHED, null, subMatch);
            }
        });

        if (result == null) {
            throw new PersistenceException();
        }

        ClickDTO clickDTO = new ClickDTO();
        clickDTO.setClickResult(result.getClickResult());

        if (clickDTO.getClickResult() == ClickResult.MISSED) {
            return clickDTO;
        } else if (clickDTO.getClickResult() == ClickResult.CLICKED) {
            Pushable pushable = modelMapper.map(result.getSwipe(), SwipeDTO.class);
            stompService.push(pushable, locale);
            return clickDTO;
        } else {
            Match match = result.getMatch();
            MatchDTO matchDTO = modelMapper.map(match, MatchDTO.class);
            clickDTO.setMatchDTO(matchDTO);

            match.swap();
            Pushable pushable = modelMapper.map(match, MatchDTO.class);
            stompService.push(pushable, locale);
            return clickDTO;
        }
    }

    private void rechargeFreeSwipe(Wallet wallet, SwipeMeta swipeMeta) {
        Date now = new Date();
        long elapsedTime = now.getTime() - wallet.getFreeSwipeRechargedAt().getTime();
        if (elapsedTime > swipeMeta.getFreeSwipePeriod()) {
            wallet.setFreeSwipe(swipeMeta.getMaxFreeSwipe());
            wallet.setFreeSwipeRechargedAt(now);
        }
    }

    private void validateSwiped(Account swiped) {
        if (swiped == null) {
            throw new SwipedNotFoundException();
        } else if (swiped.isDeleted()) {
            throw new SwipedDeletedException();
        } else if (swiped.isBlocked()) {
            throw new SwipedBlockedException();
        }
    }
}
