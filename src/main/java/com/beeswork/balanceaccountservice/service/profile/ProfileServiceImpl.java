package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;
import com.beeswork.balanceaccountservice.dto.profile.RecommendDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.profile.ProfileNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.util.DateUtil;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProfileServiceImpl extends BaseServiceImpl implements ProfileService {

    private static final int PAGE_LIMIT = 15;

    private static final int SRID = 4326;

    private static final int MAX_DISTANCE = 10000;
    private static final int MIN_DISTANCE = 1000;

    private static final double DEFAULT_LATITUDE  = 37.504508;
    private static final double DEFAULT_LONGITUDE = 127.048992;

    private static final int DEFAULT_OFFSET = 0;

    private final AccountDAO      accountDAO;
    private final ProfileDAO      profileDAO;
    private final GeometryFactory geometryFactory;

    @Autowired
    public ProfileServiceImpl(ModelMapper modelMapper,
                              GeometryFactory geometryFactory, AccountDAO accountDAO,
                              ProfileDAO profileDAO) {
        super(modelMapper);
        this.geometryFactory = geometryFactory;
        this.accountDAO = accountDAO;
        this.profileDAO = profileDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ProfileDTO getProfile(UUID accountId, UUID identityToken) {
        return modelMapper.map(findValidProfile(accountId, identityToken), ProfileDTO.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CardDTO getCard(UUID accountId, UUID identityToken, UUID swipedId) {
        Profile profile = findValidProfile(accountId, identityToken);
        CardDTO cardDTO = profileDAO.findCardDTO(swipedId, profile.getLocation());
        if (cardDTO == null) throw new ProfileNotFoundException();
        return cardDTO;
    }

    //  DESC 1. when registering, an account will be created with enabled = false, then when finish profiles,
    //          it will update enabled = true because users might get cards for which profile has not been updated
    //  TEST 2. save account without any changes but changes in accountQuestions --> Hibernate does not publish update DML for unchanged account even if you change accountQuestions
    //  TEST 3. save account without accountQuestions with modemapper --> modelmapper call setAccountQuestions and Hibernate recognizes this call and
    //          update persistence context which will delete all accountQuestions. Without modelmapper and update account fields only
    //          then Hibernate won't delete accountQuestions even if their size = 0
    @Override
    @Transactional
    public void saveProfile(UUID accountId,
                            UUID identityToken,
                            String name,
                            Date birth,
                            String about,
                            int height,
                            boolean gender) {
        Account account = accountDAO.findById(accountId);
        validateAccount(account, identityToken);
        if (profileDAO.existsById(accountId)) return;
        int birthYear = DateUtil.getYearFrom(birth);
        Point location = getLocation(DEFAULT_LONGITUDE, DEFAULT_LATITUDE);
        profileDAO.persist(new Profile(account, name, birthYear, birth, gender, height, about, location, new Date()));
    }

    @Override
    @Transactional
    public void saveAbout(UUID accountId, UUID identityToken, String about, Integer height) {
        Profile profile = profileDAO.findByIdWithLock(accountId);
        validateAccount(profile.getAccount());
        profile.setAbout(about);
        profile.setHeight(height);
    }

    @Override
    @Transactional
    public void saveLocation(UUID accountId, UUID identityToken, double latitude, double longitude, Date updatedAt) {
        Profile profile = profileDAO.findByIdWithLock(accountId);
        validateAccount(profile.getAccount());
        if (updatedAt.after(profile.getLocationUpdatedAt())) {
            profile.setLocation(getLocation(latitude, longitude));
            profile.setLocationUpdatedAt(updatedAt);
        }
    }

    private Point getLocation(double latitude, double longitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(SRID);
        return point;
    }

    // TEST 1. matches are mapped by matcher_id not matched_id
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public RecommendDTO recommend(UUID accountId, UUID identityToken, int distance, int minAge, int maxAge, boolean gender, int pageIndex) {
        Profile profile = findValidProfile(accountId, identityToken);
        RecommendDTO recommendDTO = new RecommendDTO();

        if (distance < MIN_DISTANCE) distance = MIN_DISTANCE;
        else if (distance > MAX_DISTANCE) distance = MAX_DISTANCE;
        int offset = pageIndex * PAGE_LIMIT;

        long startTime = System.currentTimeMillis();
        List<CardDTO> cardDTOs = profileDAO.findCardDTOs(distance, minAge, maxAge, gender, PAGE_LIMIT, offset, profile.getLocation());
//        if (cardDTOs.size() == 0 && pageIndex > 0) {
//            cardDTOs = profileDAO.findCardDTOs(distance, minAge, maxAge, gender, PAGE_LIMIT, DEFAULT_OFFSET, profile.getLocation());
//            recommendDTO.setReset(true);
//        }

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("estimatedTime: " + estimatedTime);

        recommendDTO.setCardDTOs(cardDTOs);
        return recommendDTO;
    }

    private Profile findValidProfile(UUID accountId, UUID identityToken) {
        Profile profile = profileDAO.findById(accountId);
        if (profile == null) throw new AccountNotFoundException();
        validateAccount(profile.getAccount(), identityToken);
        return profile;
    }

}

//TODO: chekc join photh or profile first and then query photo