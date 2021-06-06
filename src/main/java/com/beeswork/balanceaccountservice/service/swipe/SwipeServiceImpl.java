package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeMetaDAO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ListSwipesDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.Wallet;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeId;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeMeta;
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
import java.util.stream.Collectors;

@Service
public class SwipeServiceImpl extends BaseServiceImpl implements SwipeService {

    private final AccountDAO accountDAO;
    private final SwipeDAO swipeDAO;
    private final ChatDAO chatDAO;
    private final AccountQuestionDAO accountQuestionDAO;
    private final ProfileDAO profileDAO;
    private final SwipeMetaDAO swipeMetaDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public SwipeServiceImpl(ModelMapper modelMapper,
                            AccountDAO accountDAO,
                            SwipeDAO swipeDAO,
                            ChatDAO chatDAO,
                            AccountQuestionDAO accountQuestionDAO,
                            ProfileDAO profileDAO, SwipeMetaDAO swipeMetaDAO) {
        this.modelMapper = modelMapper;
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.chatDAO = chatDAO;
        this.accountQuestionDAO = accountQuestionDAO;
        this.profileDAO = profileDAO;
        this.swipeMetaDAO = swipeMetaDAO;
    }

    @Override
    @Transactional
    public List<QuestionDTO> swipe(UUID accountId, UUID identityToken, UUID swipedId) {
        Account swiper = validateAccount(accountDAO.findById(accountId), identityToken);
        Account swiped = validateSwiped(accountDAO.findById(swipedId));

        Wallet wallet = swiper.getWallet();
        SwipeMeta swipeMeta = swipeMetaDAO.findFirst();
        rechargeFreeSwipe(wallet, swipeMeta);

        if (wallet.getFreeSwipe() < swipeMeta.getSwipePoint() && wallet.getPoint() < swipeMeta.getSwipePoint())
            throw new AccountShortOfPointException();

        List<Question> questions = accountQuestionDAO.findAllQuestionsSelected(swipedId);
        if (questions == null || questions.size() <= 0)
            throw new AccountQuestionNotFoundException();

        Swipe swipe = swipeDAO.findByIdWithLock(new SwipeId(accountId, swipedId));
        Date updatedAt = new Date();
        if (swipe == null) {
            swipe = new Swipe(swiper, swiped, updatedAt);
            Profile profile = profileDAO.findByIdWithLock(swiped.getId());
            if (profile != null) profile.incrementScore();
        } else if (swipe.isMatched()) throw new SwipeMatchedExistsException();
        else if (swipe.isClicked()) throw new SwipeClickedExistsException();

        swipe.setCount((swipe.getCount() + 1));
        swipeDAO.persist(swipe);
        return modelMapper.map(questions, new TypeToken<List<QuestionDTO>>() {}.getType());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ListSwipesDTO listSwipes(UUID accountId, UUID identityToken, Date fetchedAt) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        List<SwipeDTO> swipeDTOs = swipeDAO.findSwipes(accountId, fetchedAt);
        ListSwipesDTO listSwipesDTO = new ListSwipesDTO(fetchedAt);
        if (swipeDTOs == null) return listSwipesDTO;
        List<UUID> swipedIds = swipeDTOs.stream().map(SwipeDTO::getSwipedId).collect(Collectors.toList());
        listSwipesDTO.setSwipedIds(swipedIds);
        for (SwipeDTO swipeDTO : swipeDTOs) {
            Date updatedAt = swipeDTO.getUpdatedAt();
            if (updatedAt != null && updatedAt.after(listSwipesDTO.getFetchedAt()))
                listSwipesDTO.setFetchedAt(swipeDTO.getUpdatedAt());
        }
        return listSwipesDTO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<SwipeDTO> listClicks(UUID accountId, UUID identityToken, Date fetchedAt) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        return swipeDAO.findClicks(accountId, fetchedAt);
    }

    @Override
    @Transactional
    public ClickDTO click(UUID accountId, UUID identityToken, UUID swipedId, Map<Integer, Boolean> answers) {
        Swipe subSwipe, objSwipe;
        if (accountId.compareTo(swipedId) > 0) {
            subSwipe = swipeDAO.findByIdWithLock(new SwipeId(accountId, swipedId));
            objSwipe = swipeDAO.findByIdWithLock(new SwipeId(swipedId, accountId));
        } else {
            objSwipe = swipeDAO.findByIdWithLock(new SwipeId(swipedId, accountId));
            subSwipe = swipeDAO.findByIdWithLock(new SwipeId(accountId, swipedId));
        }

        if (subSwipe == null) throw new SwipeNotFoundException();
        else if (subSwipe.isClicked()) throw new SwipeClickedExistsException();
        else if (subSwipe.isMatched()) throw new SwipeMatchedExistsException();

        Account swiper = validateAccount(subSwipe.getSwiper(), identityToken);
        Account swiped = validateSwiped(subSwipe.getSwiped());
        Wallet wallet = swiper.getWallet();
        SwipeMeta swipeMeta = swipeMetaDAO.findFirst();

        if (wallet.getFreeSwipe() >= swipeMeta.getSwipePoint())
            wallet.setFreeSwipe((wallet.getFreeSwipe() - swipeMeta.getSwipePoint()));
        else if (wallet.getPoint() < swipeMeta.getSwipePoint())
            throw new AccountShortOfPointException();
        else wallet.setPoint((wallet.getPoint() - swipeMeta.getSwipePoint()));

        ClickDTO clickDTO = new ClickDTO();
        if (accountQuestionDAO.countAllByAnswers(swipedId, answers) != answers.size()) {
            clickDTO.setSubMatchDTO(new MatchDTO(PushType.MISSED));
            return clickDTO;
        }

        Date updatedAt = new Date();
        subSwipe.setClicked(true);
        subSwipe.setUpdatedAt(updatedAt);

        if (objSwipe == null || !objSwipe.isClicked()) {
            clickDTO.setSubMatchDTO(new MatchDTO(PushType.CLICKED));
            MatchDTO objMatchDTO = new MatchDTO(PushType.CLICKED);
            objMatchDTO.setSwiperId(swiper.getId());
            objMatchDTO.setSwipedId(swiped.getId());
            objMatchDTO.setName(swiper.getName());
            objMatchDTO.setProfilePhotoKey(swiper.getProfilePhotoKey());
            objMatchDTO.setUpdatedAt(updatedAt);
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

            MatchDTO subMatchDTO = modelMapper.map(subMatch, MatchDTO.class);
            subMatchDTO.setPushType(PushType.MATCHED);
//            subMatchDTO.setSwipedId(null);
            subMatchDTO.setName(swiped.getName());
            subMatchDTO.setProfilePhotoKey(swiped.getProfilePhotoKey());
            subMatchDTO.setChatId(chat.getId());
            clickDTO.setSubMatchDTO(subMatchDTO);

            MatchDTO objMatchDTO = modelMapper.map(subMatch, MatchDTO.class);
//            objMatchDTO.setSwiperId(swiped.getId());
            objMatchDTO.setPushType(PushType.MATCHED);
            objMatchDTO.setName(swiper.getName());
            objMatchDTO.setProfilePhotoKey(swiper.getProfilePhotoKey());
            objMatchDTO.setChatId(chat.getId());
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
}
