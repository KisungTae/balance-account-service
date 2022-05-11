package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.constant.ClickOutcome;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeMetaDAO;
import com.beeswork.balanceaccountservice.dao.wallet.WalletDAO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.question.ListQuestionsDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.*;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.Wallet;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeMeta;
import com.beeswork.balanceaccountservice.exception.InternalServerException;
import com.beeswork.balanceaccountservice.exception.account.AccountQuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.*;
import com.beeswork.balanceaccountservice.exception.wallet.WalletNotFoundException;
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
import java.util.concurrent.*;

@Service
public class SwipeServiceImpl extends BaseServiceImpl implements SwipeService {

    private final AccountDAO                 accountDAO;
    private final SwipeDAO                   swipeDAO;
    private final MatchDAO                   matchDAO;
    private final AccountQuestionDAO         accountQuestionDAO;
    private final ProfileDAO                 profileDAO;
    private final SwipeMetaDAO               swipeMetaDAO;
    private final WalletDAO                  walletDAO;
    private final ModelMapper                modelMapper;
    private final StompService               stompService;
    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate        transactionTemplate;

    @Autowired
    public SwipeServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            SwipeDAO swipeDAO,
                            MatchDAO matchDAO,
                            AccountQuestionDAO accountQuestionDAO,
                            ProfileDAO profileDAO,
                            SwipeMetaDAO swipeMetaDAO,
                            WalletDAO walletDAO,
                            StompService stompService,
                            PlatformTransactionManager transactionManager) {
        this.modelMapper = modelMapper;
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.matchDAO = matchDAO;
        this.accountQuestionDAO = accountQuestionDAO;
        this.profileDAO = profileDAO;
        this.swipeMetaDAO = swipeMetaDAO;
        this.walletDAO = walletDAO;
        this.stompService = stompService;
        this.transactionManager = transactionManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public ListQuestionsDTO like(UUID swiperId, UUID swipedId, Locale locale) {
        LikeTransactionResult result = transactionTemplate.execute(status -> {
            Account swiper = accountDAO.findById(swiperId, false);
            Account swiped = accountDAO.findById(swipedId, false);
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
                swipeDAO.persist(swipe);
            } else if (swipe.isMatched()) {
                throw new SwipeMatchedExistsException();
            } else if (swipe.isClicked()) {
                throw new SwipeClickedExistsException();
            }
            swipe.setCount((swipe.getCount() + 1));
            return new LikeTransactionResult(questions, wallet.getPoint(), swipe);
        });

        if (result == null) {
            throw new PersistenceException();
        }

        SwipeDTO swipeDTO = modelMapper.map(result.getSwipe(), SwipeDTO.class);
        stompService.push(swipeDTO, locale);

        ListQuestionsDTO listQuestionsDTO = new ListQuestionsDTO();
        listQuestionsDTO.setQuestionDTOs(modelMapper.map(result.getQuestions(), new TypeToken<List<QuestionDTO>>() {}.getType()));
        listQuestionsDTO.setPoint(result.getPoint());
        return listQuestionsDTO;
    }

    @Override
    @SuppressWarnings("Duplicates")
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

            // NOTE 1.swipeDAO.findBy does not fetch join Account because Account is cached
            Account swiper = subSwipe.getSwiper();
            Account swiped = subSwipe.getSwiped();
            validateSwiped(swiped);

            Wallet wallet = walletDAO.findByAccountId(swiper.getId(), true);
            SwipeMeta swipeMeta = swipeMetaDAO.findFirst();
            rechargeFreeSwipe(wallet, swipeMeta);

            if (wallet.getFreeSwipe() >= swipeMeta.getSwipePoint()) {
                wallet.setFreeSwipe((wallet.getFreeSwipe() - swipeMeta.getSwipePoint()));
            } else if (wallet.getPoint() < swipeMeta.getSwipePoint()) {
                throw new AccountShortOfPointException();
            } else {
                wallet.setPoint((wallet.getPoint() - swipeMeta.getSwipePoint()));
            }

            if (accountQuestionDAO.countAllByAnswers(swipedId, answers) != answers.size()) {
                return new ClickTransactionResult(ClickOutcome.MISSED, null, null, null);
            }

            Date now = new Date();
            subSwipe.setClicked(true);
            subSwipe.setUpdatedAt(now);

            if (objSwipe == null || !objSwipe.isClicked()) {
                return new ClickTransactionResult(ClickOutcome.CLICKED, subSwipe, null, null);
            } else {
                UUID chatId = UUID.randomUUID();
                Match subMatch = new Match(swiper, swiped, chatId, now);
                Match objMatch = new Match(swiped, swiper, chatId, now);
                matchDAO.persist(subMatch);
                matchDAO.persist(objMatch);

                subSwipe.setMatched(true);
                objSwipe.setMatched(true);
                objSwipe.setUpdatedAt(now);

                return new ClickTransactionResult(ClickOutcome.MATCHED, null, subMatch, objMatch);
            }
        });

        if (result == null) {
            throw new PersistenceException();
        }

        ClickDTO clickDTO = new ClickDTO();
        clickDTO.setClickOutcome(result.getClickOutcome());

        if (clickDTO.getClickOutcome() == ClickOutcome.MISSED) {
            return clickDTO;
        } else if (clickDTO.getClickOutcome() == ClickOutcome.CLICKED) {
            Pushable pushable = modelMapper.map(result.getSwipe(), SwipeDTO.class);
            stompService.push(pushable, locale);
            return clickDTO;
        } else {
            MatchDTO matchDTO = modelMapper.map(result.getSubMatch(), MatchDTO.class);
            clickDTO.setMatchDTO(matchDTO);

            Pushable pushable = modelMapper.map(result.getObjMatch(), MatchDTO.class);
            stompService.push(pushable, locale);
            return clickDTO;
        }
    }

    private void rechargeFreeSwipe(Wallet wallet, SwipeMeta swipeMeta) {
        if (wallet == null) {
            throw new WalletNotFoundException();
        }
        if (swipeMeta == null) {
            throw new SwipeMetaNotFoundException();
        }
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

    @Override
    public ListSwipesDTO listSwipes(final UUID swipedId, final int startPosition, final int loadSize) {
        return getListSwipesDTO(() -> doListSwipes(swipedId, startPosition, loadSize), swipedId);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<SwipeDTO> doListSwipes(UUID swipedId, int startPosition, int loadSize) {
        List<SwipeDTO> swipes = swipeDAO.findAllBy(swipedId, startPosition, loadSize);
        for (SwipeDTO swipeDTO : swipes) {
            if (swipeDTO.getSwiperDeleted()) {
                swipeDTO.setId(null);
                swipeDTO.setSwipedId(null);
                swipeDTO.setClicked(null);
                swipeDTO.setUpdatedAt(null);
                swipeDTO.setSwiperProfilePhotoKey(null);
            }
        }
        return swipes;
    }

    @Override
    public ListSwipesDTO fetchSwipes(final UUID swipedId, final Long lastSwipeId, final int loadSize) {
        return getListSwipesDTO(() -> doFetchSwipes(swipedId, lastSwipeId, loadSize), swipedId);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<SwipeDTO> doFetchSwipes(UUID swipedId, Long lastSwipeId, int loadSize) {
        return swipeDAO.findAllBy(swipedId, lastSwipeId, loadSize);
    }

    private ListSwipesDTO getListSwipesDTO(final Callable<List<SwipeDTO>> listSwipeDTOsCallable, final UUID swipedId) {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            Future<List<SwipeDTO>> listSwipesFuture = executorService.submit(listSwipeDTOsCallable);
            Future<CountSwipesDTO> countSwipesFuture = executorService.submit(() -> countSwipes(swipedId));

            ListSwipesDTO listSwipesDTO = new ListSwipesDTO();
            List<SwipeDTO> swipeDTOs = listSwipesFuture.get(1, TimeUnit.MINUTES);
            CountSwipesDTO countSwipesDTO = countSwipesFuture.get(1, TimeUnit.MINUTES);

            listSwipesDTO.setSwipeDTOs(swipeDTOs);
            listSwipesDTO.setSwipeCount(countSwipesDTO.getCount());
            listSwipesDTO.setSwipeCountCountedAt(countSwipesDTO.getCountedAt());
            return listSwipesDTO;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new InternalServerException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CountSwipesDTO countSwipes(UUID swipedId) {
        Date now = new Date();
        return new CountSwipesDTO(swipeDAO.countSwipesBy(swipedId), now);
    }
}
