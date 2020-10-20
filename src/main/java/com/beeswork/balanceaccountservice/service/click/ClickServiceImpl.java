package com.beeswork.balanceaccountservice.service.click;


import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.click.ClickDTO;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.service.account.AccountInterService;
import com.beeswork.balanceaccountservice.service.chat.ChatInterService;
import com.beeswork.balanceaccountservice.service.swipe.SwipeInterService;
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

    private final AccountInterService accountInterService;
    private final SwipeInterService swipeInterService;
    private final ChatInterService chatInterService;

    private final SwipeDAO swipeDAO;


    @Autowired
    public ClickServiceImpl(AccountInterService accountInterService,
                            SwipeInterService swipeInterService,
                            ChatInterService chatInterService,
                            SwipeDAO swipeDAO) {

        this.accountInterService = accountInterService;
        this.swipeInterService = swipeInterService;
        this.chatInterService = chatInterService;
        this.swipeDAO = swipeDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<ClickedProjection> listClicked(String clickedId, String email, Date fetchedAt)
    throws AccountInvalidException, AccountNotFoundException {

        accountInterService.checkIfValid(UUID.fromString(clickedId), email);
        return swipeDAO.findAllClickedAfter(UUID.fromString(clickedId), fetchedAt);
    }


    @Override
    @Transactional
    public List<FCMNotificationDTO> click(ClickDTO clickDTO)
    throws SwipeNotFoundException, AccountInvalidException, AccountBlockedException, AccountNotFoundException,
           SwipeClickedExistsException {

        UUID swiperUUId = UUID.fromString(clickDTO.getSwiperId());
        UUID swipedUUId = UUID.fromString(clickDTO.getSwipedId());

        Swipe swipe = swipeDAO.findByIdWithAccounts(clickDTO.getSwipeId(), swiperUUId, swipedUUId);

        if (swipe.isClicked()) throw new SwipeClickedExistsException();

        Account swiper = swipe.getSwiper();
        Account swiped = swipe.getSwiped();

        accountInterService.checkIfValid(swiper, clickDTO.getSwiperEmail());
        accountInterService.checkIfBlocked(swiped);

        swipe.setClicked(true);
        swipeDAO.persist(swipe);

        List<FCMNotificationDTO> fcmNotificationDTOs = new ArrayList<>();

        // match
        if (swipeInterService.existsByClicked(swipedUUId, swiperUUId, true)) {

            Chat chat = new Chat();
            Date date = new Date();
            swipe.getSwiper().getMatches().add(new Match(swiper, swiped, chat, false, date, date));
            swipe.getSwiped().getMatches().add(new Match(swiped, swiper, chat, false, date, date));

            chatInterService.persist(chat);
            accountInterService.persist(swiper);
            accountInterService.persist(swiped);

            fcmNotificationDTOs.add(FCMNotificationDTO.matchNotification(swiper.getFcmToken(), swiped.getRepPhotoKey()));
            fcmNotificationDTOs.add(FCMNotificationDTO.matchNotification(swiped.getFcmToken(), swiper.getRepPhotoKey()));

        } else {
            fcmNotificationDTOs.add(FCMNotificationDTO.clickedNotification(swiped.getFcmToken(), swiper.getRepPhotoKey()));
        }

        return fcmNotificationDTOs;
    }

}
