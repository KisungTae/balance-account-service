package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.constant.NotificationType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
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

    private final AccountDAO accountDAO;
    private final SwipeDAO swipeDAO;

    @Autowired
    public SwipeServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, SwipeDAO swipeDAO) {

        super(modelMapper);
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
    }

    @Override
    @Transactional
    public List<QuestionDTO> swipe(String swiperId, String swiperEmail, String swipedId) {

        Account swiper = accountDAO.findBy(UUID.fromString(swiperId), swiperEmail);
        checkIfValid(swiper);

        Account swiped = accountDAO.findWithQuestions(UUID.fromString(swipedId), null);
        checkIfValid(swiped);

        if (swipeDAO.existsByClicked(swiper.getId(), swiped.getId(), true))
            throw new SwipeClickedExistsException();

//        if (swiper.getPoint() < AppConstant.SWIPE_POINT)
//            throw new AccountShortOfPointException();

        Swipe swipe = new Swipe(swiper, swiped, false, new Date(), new Date());
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
    public List<ClickedProjection> listClicked(String swipedId, String email, Date fetchedAt) {

        UUID swipedUUID = UUID.fromString(swipedId);

        if (!accountDAO.existsBy(swipedUUID, email, true))
            throw new AccountInvalidException();

        return swipeDAO.findAllClickedAfter(swipedUUID, fetchedAt);
    }

    @Override
    @Transactional
    public ClickDTO click(Long swipeId, String swiperId, String swiperEmail, String swipedId, Map<Long, Boolean> answers) {

        UUID swiperUUId = UUID.fromString(swiperId);
        UUID swipedUUId = UUID.fromString(swipedId);

        Swipe swipe = swipeDAO.findWithAccounts(swipeId, swiperUUId, swipedUUId);

        Account swiper = swipe.getSwiper();
        Account swiped = swipe.getSwiped();

        checkIfValid(swiper, swiperEmail);
        checkIfValid(swiped, null);

//        if (swiper.getPoint() < AppConstant.SWIPE_POINT)
//            throw new AccountShortOfPointException();

        int point = swiper.getPoint();
        point -= AppConstant.SWIPE_POINT;
        swiper.setPoint(point);

        ClickDTO clickDTO = new ClickDTO();
        clickDTO.setClicked(true);

        for (AccountQuestion accountQuestion : swiped.getAccountQuestions()) {
            Boolean answer = answers.get(accountQuestion.getQuestionId());
            if (answer == null || accountQuestion.isSelected() != answer) {
                clickDTO.setClicked(false);
                break;
            }
        }

        if (!clickDTO.isClicked()) return clickDTO;

        // match
        if (swipeDAO.existsByClicked(swipedUUId, swiperUUId, true)) {

            Chat chat = new Chat();
            Date date = new Date();

            swiper.getMatches().add(new Match(swiper, swiped, chat, false, date, date));
            swiped.getMatches().add(new Match(swiped, swiper, chat, false, date, date));

            clickDTO.setupAsMatch(swiped.getId(), swiped.getRepPhotoKey(), swiped.getFcmToken());
        } else {

            clickDTO.setupAsClicked(swiped.getId(), swiped.getRepPhotoKey(), swiped.getFcmToken());
        }

        return clickDTO;
    }
}
