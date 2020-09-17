package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;


@Service
public class MatchServiceImpl extends BaseServiceImpl implements MatchService {

    private final AccountDAO accountDAO;
    private final SwipeDAO swipeDAO;
    private final MatchDAO matchDAO;

    @Autowired
    public MatchServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, SwipeDAO swipeDAO, MatchDAO matchDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.matchDAO = matchDAO;
    }

    @Override
    @Transactional
    public MatchDTO balance(SwipeDTO swipeDTO)
    throws SwipeNotFoundException, AccountInvalidException, MatchExistsException {

        UUID swiperUUId = UUID.fromString(swipeDTO.getSwiperId());
        UUID swipedUUId = UUID.fromString(swipeDTO.getSwipedId());

        Swipe swipe = swipeDAO.findByIdWithAccounts(swipeDTO.getSwipeId(), swiperUUId, swipedUUId);

        Account swiper = swipe.getSwiper();
        Account swiped = swipe.getSwiped();

//        if (!swiper.getEmail().equals(swipeDTO.getSwiperEmail()))
//            throw new AccountInvalidException();

        swipe.setBalanced(true);
        swipeDAO.persist(swipe);

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setMatched(false);

        // match
        if (swipeDAO.existsByAccountIdsAndBalanced(swipedUUId, swiperUUId, true)) {

            if (matchDAO.existsById(new MatchId(swiperUUId, swipedUUId)))
                throw new MatchExistsException();

            Date date = new Date();
            swipe.getSwiper().getMatches().add(new Match(swiper, swiped, false, date));
            swipe.getSwiped().getMatches().add(new Match(swiped, swiper, false, date));

            accountDAO.persist(swiper);
            accountDAO.persist(swiped);

            matchDTO.setMatchedId(swipedUUId.toString());
            matchDTO.setMatchedImageUrl(AppConstant.AWS_S3_URL);
        }

        return matchDTO;
    }




}
