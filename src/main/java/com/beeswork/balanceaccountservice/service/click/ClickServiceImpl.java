package com.beeswork.balanceaccountservice.service.click;


import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.click.ClickDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ClickServiceImpl implements ClickService {

    private final AccountDAO accountDAO;
    private final SwipeDAO swipeDAO;
    private final MatchDAO matchDAO;

    @Autowired
    public ClickServiceImpl(AccountDAO accountDAO, SwipeDAO swipeDAO, MatchDAO matchDAO) {
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.matchDAO = matchDAO;
    }

    @Override
    @Transactional
    public List<String> listClick(String accountId) {

        List<String> swipeIds = new ArrayList<>();
        for (Swipe swipe : swipeDAO.findAllClicked(UUID.fromString(accountId))) {
            swipeIds.add(swipe.getSwiperId().toString());
        }
        return swipeIds;
    }


    @Override
    @Transactional
    public void click(ClickDTO clickDTO)
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

        // match
        if (swipeDAO.existsByAccountIdsAndClicked(swipedUUId, swiperUUId, true)) {

            if (matchDAO.existsById(new MatchId(swiperUUId, swipedUUId)))
                throw new MatchExistsException();

            Date date = new Date();
            swipe.getSwiper().getMatches().add(new Match(swiper, swiped, false, date));
            swipe.getSwiped().getMatches().add(new Match(swiped, swiper, false, date));

            accountDAO.persist(swiper);
            accountDAO.persist(swiped);
        }
    }
}