package com.beeswork.balanceaccountservice.service.click;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.photo.PhotoDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.click.ClickDTO;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ClickServiceImpl implements ClickService {

    private final AccountDAO accountDAO;
    private final SwipeDAO   swipeDAO;
    private final MatchDAO   matchDAO;
    private final ChatDAO    chatDAO;
    private final PhotoDAO   photoDAO;

    @Autowired
    public ClickServiceImpl(AccountDAO accountDAO, SwipeDAO swipeDAO, MatchDAO matchDAO,
                            ChatDAO chatDAO, PhotoDAO photoDAO) {
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.matchDAO = matchDAO;
        this.chatDAO = chatDAO;
        this.photoDAO = photoDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ClickedProjection> listClicked(String clickedId, String email, Date fetchedAt)
    throws AccountInvalidException {

        if (!accountDAO.existsByIdAndEmail(UUID.fromString(clickedId), email))
            throw new AccountInvalidException();

        return swipeDAO.findAllClickedAfter(UUID.fromString(clickedId), fetchedAt);
    }

    @Override
    @Transactional
    public List<FCMNotificationDTO> click(ClickDTO clickDTO)
    throws SwipeNotFoundException, AccountInvalidException, MatchExistsException {

        UUID swiperUUId = UUID.fromString(clickDTO.getSwiperId());
        UUID swipedUUId = UUID.fromString(clickDTO.getSwipedId());

        Swipe swipe = swipeDAO.findByIdWithAccounts(clickDTO.getSwipeId(), swiperUUId, swipedUUId);

        Account swiper = swipe.getSwiper();
        Account swiped = swipe.getSwiped();

//        if (!swiper.getEmail().equals(swipeDTO.getSwiperEmail()))
//            throw new AccountInvalidException();

        swipe.setClicked(true);
        swipeDAO.persist(swipe);

        List<FCMNotificationDTO> fcmNotificationDTOs = new ArrayList<>();

        // match
        if (swipeDAO.existsByAccountIdsAndClicked(swipedUUId, swiperUUId, true)) {

            if (matchDAO.existsById(new MatchId(swiperUUId, swipedUUId)))
                throw new MatchExistsException();

            Chat chat = new Chat();

            Date date = new Date();
            swipe.getSwiper().getMatches().add(new Match(swiper, swiped, chat, false, date, date));
            swipe.getSwiped().getMatches().add(new Match(swiped, swiper, chat, false, date, date));

            chatDAO.persist(chat);
            accountDAO.persist(swiper);
            accountDAO.persist(swiped);

            fcmNotificationDTOs.add(FCMNotificationDTO.matchNotification(swiper.getFcmToken(), swiped.getRepPhotoKey()));
            fcmNotificationDTOs.add(FCMNotificationDTO.matchNotification(swiped.getFcmToken(), swiper.getRepPhotoKey()));

        } else {
            fcmNotificationDTOs.add(FCMNotificationDTO.clickedNotification(swiped.getFcmToken(), swiper.getRepPhotoKey()));
        }

        return fcmNotificationDTOs;
    }

}
