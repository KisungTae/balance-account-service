package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.constant.ColumnIndex;
import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.account.AccountProfileDTO;
import com.beeswork.balanceaccountservice.dto.account.PhotoDTO;
import com.beeswork.balanceaccountservice.dto.match.BalanceDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.match.SwipeDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeBalancedExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
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
public class MatchServiceImpl extends BaseServiceImpl implements MatchService {

    private final AccountDAO accountDAO;
    private final SwipeDAO swipeDAO;
    private final MatchDAO matchDAO;

    private final GeometryFactory geometryFactory;

    @Autowired
    public MatchServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, SwipeDAO swipeDAO, MatchDAO matchDAO, GeometryFactory geometryFactory) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.swipeDAO = swipeDAO;
        this.matchDAO = matchDAO;
        this.geometryFactory = geometryFactory;
    }

    // TEST 1. matches are mapped by matcher_id not matched_id
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<AccountProfileDTO> recommend(String accountId, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude)
    throws AccountNotFoundException {

        UUID accountUUID = UUID.fromString(accountId);

        Account account = accountDAO.findById(accountUUID);
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        location.setSRID(AppConstant.SRID);

        List<Object[]> accounts = accountDAO.findAllWithin(accountUUID, distance, minAge, maxAge, gender, AppConstant.LIMIT,
                                                           AppConstant.LIMIT * account.getIndex(), location);

        String previousId = "";
        List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
        AccountProfileDTO accountProfileDTO = new AccountProfileDTO();

        for (Object[] cAccount : accounts) {
            String id = cAccount[ColumnIndex.ACCOUNT_PROFILE_ID].toString();
            if (!previousId.equals(id)) {

                accountProfileDTOs.add(accountProfileDTO);
                previousId = id;

                String name = cAccount[ColumnIndex.ACCOUNT_PROFILE_NAME].toString();
                String about = cAccount[ColumnIndex.ACCOUNT_PROFILE_ABOUT].toString();
                int distanceBetween = (int) ((double) cAccount[ColumnIndex.ACCOUNT_PROFILE_DISTANCE]);

                accountProfileDTO = new AccountProfileDTO(id, name, about, distanceBetween);
            }

            String url = cAccount[ColumnIndex.ACCOUNT_PROFILE_PHOTO_URL].toString();
            int sequence = (int) cAccount[ColumnIndex.ACCOUNT_PROFILE_PHOTO_SEQUENCE];
            PhotoDTO photoDTO = new PhotoDTO(url, sequence);
            accountProfileDTO.getPhotoDTOs().add(photoDTO);
        }

        accountProfileDTOs.add(accountProfileDTO);
        accountProfileDTOs.remove(0);
        return accountProfileDTOs;
    }

    @Override
    @Transactional
    public BalanceDTO swipe(SwipeDTO swipeDTO)
    throws AccountNotFoundException, AccountInvalidException, SwipeBalancedExistsException,
           AccountShortOfPointException {

        UUID swiperUUId = UUID.fromString(swipeDTO.getSwiperId());
        UUID swipedUUId = UUID.fromString(swipeDTO.getSwipedId());

        Account swiper = accountDAO.findById(swiperUUId);

//        if (!swiper.getEmail().equals(swipeDTO.getSwiperEmail()))
//            throw new AccountInvalidException();

        if (swipeDAO.balancedExists(swiperUUId, swipedUUId))
            throw new SwipeBalancedExistsException();

        int currentPoint = swiper.getPoint();

//        if (currentPoint < AppConstant.SWIPE_POINT)
//            throw new AccountShortOfPointException();

        Account swiped = accountDAO.findByIdWithQuestions(swipedUUId);

        Swipe swipe = new Swipe(swiper, swiped, false, new Date(), new Date());
        swiper.getSwipes().add(swipe);

        currentPoint -= AppConstant.SWIPE_POINT;
        swiper.setPoint(currentPoint);

        accountDAO.persist(swiper);

        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setSwipeId(swipe.getId());
        balanceDTO.setSwipedId(swiped.getId().toString());

        for (AccountQuestion accountQuestion : swiped.getAccountQuestions()) {
            Question question = accountQuestion.getQuestion();
            QuestionDTO questionDTO = new QuestionDTO(question.getDescription(),
                                                      question.getTopOption(),
                                                      question.getBottomOption(),
                                                      accountQuestion.isSelected(),
                                                      accountQuestion.getSequence());
            balanceDTO.getQuestionDTOs().add(questionDTO);
        }

        return balanceDTO;
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
//            matchDTO.setMatchedImageUrl(swiped);
        }

        return matchDTO;
    }


}
