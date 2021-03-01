package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.PreRecommendDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;
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

    private static final int PAGE_LIMIT = 15;

    private static final int SRID = 4326;

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
        int birthYear = DateUtil.getYearFrom(birth);
        Point location = getLocation(DEFAULT_LONGITUDE, DEFAULT_LATITUDE);
        profileDAO.persist(new Profile(account, name, birthYear, birth, gender, height, about, location, new Date()));
    }

    @Override
    @Transactional
    public void saveAbout(UUID accountId, UUID identityToken, String about, Integer height) {
        Profile profile = findValidProfile(accountId, identityToken);
        profile.setAbout(about);
        profile.setHeight(height);
    }

    @Override
    @Transactional
    public void saveLocation(UUID accountId, UUID identityToken, double latitude, double longitude, Date updatedAt) {
        saveLocation(findValidProfile(accountId, identityToken), latitude, longitude, updatedAt);
    }

    private void saveLocation(Profile profile, double latitude, double longitude, Date updatedAt) {
        profile.setLocation(getLocation(latitude, longitude));
        profile.setLocationUpdatedAt(updatedAt);
    }

    private Point getLocation(double latitude, double longitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(SRID);
        return point;
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
        int offset = pageIndex * PAGE_LIMIT;
        return profileDAO.findAllWithin(distance, minAge, maxAge, gender, PAGE_LIMIT, offset, location);
    }

    private Profile findValidProfile(UUID accountId, UUID identityToken) {
        Profile profile = profileDAO.findById(accountId);
        if (profile == null) throw new ProfileNotFoundException();
        validateAccount(profile.getAccount(), identityToken);
        return profile;
    }

}
