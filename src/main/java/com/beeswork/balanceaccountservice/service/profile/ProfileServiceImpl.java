package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.account.PreRecommendDTO;
import com.beeswork.balanceaccountservice.dto.account.ProfileDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
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

    private static final int CARD_ID = 0;
    private static final int CARD_NAME = 1;
    private static final int CARD_ABOUT = 2;
    private static final int CARD_BIRTH_YEAR = 3;
    private static final int CARD_DISTANCE = 4;
    private static final int CARD_PHOTO_KEY = 5;
    private static final int CARD_HEIGHT = 6;

    private static final int PAGE_LIMIT = 15;

    private static final int MAX_DISTANCE = 10000;
    private static final int MIN_DISTANCE = 1000;

    private static final double DEFAULT_LATITUDE = 37.504508;
    private static final double DEFAULT_LONGITUDE = 127.048992;

    private final AccountDAO accountDAO;
    private final ProfileDAO profileDAO;
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
        profileDAO.persist(new Profile(account,
                                       name,
                                       DateUtil.getYearFrom(birth),
                                       birth,
                                       gender,
                                       height,
                                       about,
                                       geometryFactory.createPoint(new Coordinate(DEFAULT_LONGITUDE, DEFAULT_LATITUDE)),
                                       new Date()));
    }

    @Override
    @Transactional
    public void saveAbout(UUID accountId, UUID identityToken, String about, Integer height) {
//        Profile profile = findValidProfile(accountId, identityToken);
        Account account = accountDAO.findById(accountId);
        Profile profile1 = account.getProfile();
//        profile.setAbout(about);
//        profile.setHeight(height);
    }

    @Override
    @Transactional
    public void saveLocation(UUID accountId, UUID identityToken, double latitude, double longitude, Date updatedAt) {
        saveLocation(findValidProfile(accountId, identityToken), latitude, longitude, updatedAt);
    }

    private void saveLocation(Profile profile, double latitude, double longitude, Date updatedAt) {
        profile.setLocation(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        profile.setLocationUpdatedAt(updatedAt);
    }


    @Override
    @Transactional
    public PreRecommendDTO preRecommend(UUID accountId,
                                        UUID identityToken,
                                        Double latitude,
                                        Double longitude,
                                        Date locationUpdatedAt,
                                        boolean reset) {
        Profile profile = findValidProfile(accountId, identityToken);
        PreRecommendDTO preRecommendDTO = new PreRecommendDTO();
        preRecommendDTO.setPageIndex((reset ? 0 : profile.getPageIndex()));
        profile.setPageIndex((preRecommendDTO.getPageIndex() + 1));

        if (latitude != null &&
            longitude != null &&
            locationUpdatedAt != null &&
            locationUpdatedAt.after(profile.getLocationUpdatedAt()))
            saveLocation(profile, latitude, longitude, locationUpdatedAt);

        preRecommendDTO.setLocation(profile.getLocation());
        return preRecommendDTO;
    }


    // TEST 1. matches are mapped by matcher_id not matched_id
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<CardDTO> recommend(int distance,
                                   int minAge,
                                   int maxAge,
                                   boolean gender,
                                   Point location,
                                   int pageIndex) {
        if (distance < MIN_DISTANCE) distance = MIN_DISTANCE;
        else if (distance > MAX_DISTANCE) distance = MAX_DISTANCE;

        List<Object[]> accounts = profileDAO.findAllWithin(distance,
                                                           minAge,
                                                           maxAge,
                                                           gender,
                                                           PAGE_LIMIT,
                                                           pageIndex * PAGE_LIMIT,
                                                           location);

        String previousId = "";
        List<CardDTO> cardDTOs = new ArrayList<>();
        CardDTO cardDTO = new CardDTO();

        for (Object[] cAccount : accounts) {
            String id = cAccount[CARD_ID].toString();
            if (!previousId.equals(id)) {

                cardDTOs.add(cardDTO);
                previousId = id;

                String name = cAccount[CARD_NAME].toString();
                String about = cAccount[CARD_ABOUT].toString();
                int birthYear = Integer.parseInt(cAccount[CARD_BIRTH_YEAR].toString());
                int distanceBetween = (int) ((double) cAccount[CARD_DISTANCE]);
                Integer height = (Integer) cAccount[CARD_HEIGHT];

                cardDTO = new CardDTO(id, name, about, height, birthYear, distanceBetween);
            }

            if (cAccount[CARD_PHOTO_KEY] != null)
                cardDTO.getPhotos().add(cAccount[CARD_PHOTO_KEY].toString());
        }

        cardDTOs.add(cardDTO);
        cardDTOs.remove(0);
        return cardDTOs;
    }

    private Profile findValidProfile(UUID accountId, UUID identityToken) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        Profile profile = profileDAO.findById(accountId);
        if (profile == null) throw new ProfileNotFoundException();
        return profile;
    }

}
