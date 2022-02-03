package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeMetaDAO;
import com.beeswork.balanceaccountservice.dao.wallet.WalletDAO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.dto.swipe.CountClicksDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
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

    private final AccountDAO         accountDAO;
    private final SwipeDAO           swipeDAO;
    private final ChatDAO            chatDAO;
    private final AccountQuestionDAO accountQuestionDAO;
    private final ProfileDAO         profileDAO;
    private final SwipeMetaDAO       swipeMetaDAO;
    private final WalletDAO          walletDAO;
    private final ModelMapper        modelMapper;

    @Autowired
    public SwipeServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            SwipeDAO swipeDAO,
                            ChatDAO chatDAO,
                            AccountQuestionDAO accountQuestionDAO,
                            ProfileDAO profileDAO, SwipeMetaDAO swipeMetaDAO,
                            WalletDAO walletDAO) {
        this.modelMapper = modelMapper;
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.chatDAO = chatDAO;
        this.accountQuestionDAO = accountQuestionDAO;
        this.profileDAO = profileDAO;
        this.swipeMetaDAO = swipeMetaDAO;
        this.walletDAO = walletDAO;
    }

    @Override
    @Transactional
    public List<QuestionDTO> swipe(UUID swiperId, UUID swipedId) {
        Account swiper = accountDAO.findById(swiperId);
        Account swiped = accountDAO.findById(swipedId);
        validateSwiped(swiped);

        Wallet wallet = walletDAO.findByAccountId(swiper.getId());
        SwipeMeta swipeMeta = swipeMetaDAO.findFirst();
        rechargeFreeSwipe(wallet, swipeMeta);

        if (wallet.getFreeSwipe() < swipeMeta.getSwipePoint() && wallet.getPoint() < swipeMeta.getSwipePoint()) {
            throw new AccountShortOfPointException();
        }

        List<Question> questions = accountQuestionDAO.findAllQuestionsSelected(swipedId);
        if (questions == null || questions.size() <= 0) {
            throw new AccountQuestionNotFoundException();
        }

        Swipe swipe = swipeDAO.findBySwiperIdAndSwipedId(swiperId, swipedId, true);
        Date updatedAt = new Date();
        if (swipe == null) {
            swipe = new Swipe(swiper, swiped, updatedAt);
            Profile profile = profileDAO.findByIdWithLock(swiped.getId());
            if (profile != null) {
                profile.incrementScore();
            }
        } else if (swipe.isMatched()) {
            throw new SwipeMatchedExistsException();
        } else if (swipe.isClicked()) {
            throw new SwipeClickedExistsException();
        }

        swipe.setCount((swipe.getCount() + 1));
        swipeDAO.persist(swipe);
        return modelMapper.map(questions, new TypeToken<List<QuestionDTO>>() {}.getType());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<SwipeDTO> listClicks(UUID accountId, int startPosition, int loadSize) {
        List<SwipeDTO> clicks = swipeDAO.findClicks(accountId, startPosition, loadSize);
        for (SwipeDTO click : clicks) {
            if (click.getDeleted()) {
                click.setSwipedId(null);
                click.setName(null);
                click.setProfilePhotoKey(null);
            }
        }
        return swipeDAO.findClicks(accountId, startPosition, loadSize);
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
    @Transactional
    public ClickDTO click(UUID swiperId, UUID swipedId, Map<Integer, Boolean> answers) {
        Swipe subSwipe, objSwipe;
        if (swiperId.compareTo(swipedId) > 0) {
            subSwipe = swipeDAO.findBySwiperIdAndSwipedId(swiperId, swipedId, true);
            objSwipe = swipeDAO.findBySwiperIdAndSwipedId(swipedId, swiperId, true);
        } else {
            objSwipe = swipeDAO.findBySwiperIdAndSwipedId(swipedId, swiperId, true);
            subSwipe = swipeDAO.findBySwiperIdAndSwipedId(swiperId, swipedId, true);
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

        // wallet needs to be locked
        Wallet wallet = walletDAO.findByAccountId(swiper.getId());
        SwipeMeta swipeMeta = swipeMetaDAO.findFirst();

        if (wallet.getFreeSwipe() >= swipeMeta.getSwipePoint()) {
            wallet.setFreeSwipe((wallet.getFreeSwipe() - swipeMeta.getSwipePoint()));
        } else if (wallet.getPoint() < swipeMeta.getSwipePoint()) {
            throw new AccountShortOfPointException();
        } else {
            wallet.setPoint((wallet.getPoint() - swipeMeta.getSwipePoint()));
        }

        ClickDTO clickDTO = new ClickDTO();
        if (accountQuestionDAO.countAllByAnswers(swipedId, answers) != answers.size()) {
            clickDTO.setSubMatchDTO(new MatchDTO(PushType.MISSED));
            return clickDTO;
        }

        Date updatedAt = new Date();
        subSwipe.setClicked(true);
        subSwipe.setUpdatedAt(updatedAt);

        if (objSwipe == null || !objSwipe.isClicked()) {
            MatchDTO subMatchDTO = new MatchDTO(PushType.CLICKED);
            subMatchDTO.setSwiperId(swiper.getId());
            subMatchDTO.setSwipedId(swiped.getId());
            clickDTO.setSubMatchDTO(subMatchDTO);

            MatchDTO objMatchDTO = new MatchDTO(PushType.CLICKED);
            objMatchDTO.setSwiperId(swiper.getId());
            objMatchDTO.setSwipedId(swiped.getId());
            objMatchDTO.setName(swiper.getName());
            objMatchDTO.setProfilePhotoKey(swiper.getProfilePhotoKey());
            objMatchDTO.setUpdatedAt(updatedAt);
            objMatchDTO.setDeleted(false);
            clickDTO.setObjMatchDTO(objMatchDTO);
        } else {
            Chat chat = new Chat();
            chatDAO.persist(chat);

            Match subMatch = new Match(swiper, swiped, chat, updatedAt);
            Match objMatch = new Match(swiped, swiper, chat, updatedAt);
            swiper.getMatches().add(subMatch);
            swiped.getMatches().add(objMatch);

            subSwipe.setMatched(true);
            objSwipe.setMatched(true);
            objSwipe.setUpdatedAt(updatedAt);

            MatchDTO subMatchDTO = modelMapper.map(subMatch, MatchDTO.class);
            subMatchDTO.setPushType(PushType.MATCHED);
            subMatchDTO.setName(swiped.getName());
            subMatchDTO.setProfilePhotoKey(swiped.getProfilePhotoKey());
            subMatchDTO.setChatId(chat.getId());
            clickDTO.setSubMatchDTO(subMatchDTO);

            MatchDTO objMatchDTO = modelMapper.map(subMatch, MatchDTO.class);
            objMatchDTO.setPushType(PushType.MATCHED);
            objMatchDTO.setName(swiper.getName());
            objMatchDTO.setProfilePhotoKey(swiper.getProfilePhotoKey());
            objMatchDTO.setChatId(chat.getId());
            objMatchDTO.setDeleted(false);
            clickDTO.setObjMatchDTO(objMatchDTO);
        }
        return clickDTO;
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
