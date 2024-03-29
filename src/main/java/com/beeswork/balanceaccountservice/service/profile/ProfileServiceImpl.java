package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.AccountQuestionDAO;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.dao.photo.PhotoDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.entity.profile.Card;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.profile.*;
import com.beeswork.balanceaccountservice.exception.login.LoginNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.util.DateUtil;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class ProfileServiceImpl extends BaseServiceImpl implements ProfileService {

    private static final int PAGE_LIMIT = 15;

    private static final int SRID = 4326;

    private static final int MAX_DISTANCE  = 30;
    private static final int MIN_DISTANCE  = 1;
    private static final int DISTANCE_UNIT = 1000;

    private static final int MIN_AGE = 20;
    private static final int MAX_AGE = 80;

    private static final int DEFAULT_OFFSET = 0;

    private final AccountDAO         accountDAO;
    private final ProfileDAO         profileDAO;
    private final LoginDAO           loginDAO;
    private final PhotoDAO           photoDAO;
    private final AccountQuestionDAO accountQuestionDAO;
    private final GeometryFactory    geometryFactory;
    private final ModelMapper        modelMapper;
    private final AWSProperties      awsProperties;

    @Autowired
    public ProfileServiceImpl(ModelMapper modelMapper,
                              GeometryFactory geometryFactory,
                              AccountDAO accountDAO,
                              ProfileDAO profileDAO,
                              LoginDAO loginDAO,
                              PhotoDAO photoDAO,
                              AccountQuestionDAO accountQuestionDAO,
                              AWSProperties awsProperties) {
        this.modelMapper = modelMapper;
        this.geometryFactory = geometryFactory;
        this.accountDAO = accountDAO;
        this.profileDAO = profileDAO;
        this.loginDAO = loginDAO;
        this.photoDAO = photoDAO;
        this.accountQuestionDAO = accountQuestionDAO;
        this.awsProperties = awsProperties;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ProfileDTO getProfile(UUID accountId) {
        return modelMapper.map(findValidProfile(accountId, false), ProfileDTO.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CardDTO getCard(UUID accountId, UUID swipedId) {
        Profile profile = findValidProfile(accountId, false);
        Card card = profileDAO.findCard(swipedId, profile.getLocation());
        if (card == null) {
            throw new ProfileNotFoundException();
        }
        return modelMapper.map(card, CardDTO.class);
    }

    //  DESC 1. when registering, an account will be created with enabled = false, then when finish profiles,
    //          it will update enabled = true because users might get cards for which profile has not been updated
    //  TEST 2. save account without any changes but changes in accountQuestions --> Hibernate does not publish update DML for unchanged account even if you change accountQuestions
    //  TEST 3. save account without accountQuestions with modemapper --> modelmapper call setAccountQuestions and Hibernate recognizes this call and
    //          update persistence context which will delete all accountQuestions. Without modelmapper and update account fields only
    //          then Hibernate won't delete accountQuestions even if their size = 0
    //  NOTE 1. This is the only place where profile is created, so if profile already exists, it should not update profile at all
    @Override
    @Transactional
    public void saveProfile(UUID accountId,
                            String name,
                            LocalDate birthDate,
                            String about,
                            int height,
                            boolean gender,
                            double latitude,
                            double longitude) {
        if (profileDAO.existsById(accountId)) {
            throw new BadRequestException();
        }
        Account account = accountDAO.findById(accountId, true);
        account.setName(name);

        if (photoDAO.countBy(accountId) <= 0) {
            throw new NoPhotoUploadedException();
        } else if (accountQuestionDAO.countBy(accountId) <= 0) {
            throw new NoQuestionSavedException();
        }

        Point location = getLocation(latitude, longitude);
        Profile profile = new Profile(account, name, birthDate.getYear(), birthDate, gender, height, about, location, true, new Date());
        profileDAO.persist(profile);
    }

    @Override
    @Transactional
    public void saveBio(UUID accountId, String about, Integer height) {
        Profile profile = findValidProfile(accountId, true);
        profile.setAbout(about);
        profile.setHeight(height);
    }

    @Override
    @Transactional
    public void saveLocation(UUID accountId, double latitude, double longitude, Date updatedAt) {
        Profile profile = findValidProfile(accountId, true);
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
    public List<CardDTO> recommend(UUID accountId, int distance, int minAge, int maxAge, boolean gender, int pageIndex) {
        Profile profile = findValidProfile(accountId, false);

        if (distance < MIN_DISTANCE) {
            distance = MIN_DISTANCE;
        } else if (distance > MAX_DISTANCE) {
            distance = MAX_DISTANCE;
        }
        distance = distance * DISTANCE_UNIT;

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (minAge < MIN_AGE) {
            minAge = MIN_AGE;
        }
        minAge = currentYear - minAge + 1;
        if (maxAge >= MAX_AGE) {
            maxAge = 0;
        } else {
            maxAge = currentYear - maxAge + 1;
        }
        List<Card> cards = profileDAO.findCards(accountId, distance, minAge, maxAge, gender, PAGE_LIMIT, pageIndex, profile.getLocation());
        return modelMapper.map(cards, new TypeToken<List<CardDTO>>() {
        }.getType());
    }

    private Profile findValidProfile(UUID accountId, boolean writeLock) {
        Profile profile = profileDAO.findById(accountId, writeLock);
        if (profile == null) {
            throw new ProfileNotFoundException();
        }
        return profile;
    }

    @Override
    @Transactional
    public void saveEmail(UUID accountId, String email) {
        Login login = loginDAO.findByAccountId(accountId);
        if (login == null) {
            throw new LoginNotFoundException();
        }

        LoginType loginType = login.getType();
        if (loginType == LoginType.NAVER || loginType == LoginType.GOOGLE) {
            throw new EmailNotMutableException();
        }
        if (!login.getEmail().equals(email)) {
            if (loginDAO.existsByEmail(email)) {
                throw new EmailDuplicateException();
            }
            login.setEmail(email);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public String getEmail(UUID accountId) {
        Login login = loginDAO.findByAccountId(accountId);
        if (login == null) {
            throw new LoginNotFoundException();
        }
        return login.getEmail();
    }

}

//TODO: chekc join photh or profile first and then query photo