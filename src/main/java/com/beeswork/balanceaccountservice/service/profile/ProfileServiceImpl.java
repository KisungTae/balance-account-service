package com.beeswork.balanceaccountservice.service.profile;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.account.ProfileDAO;
import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.account.PreRecommendDTO;
import com.beeswork.balanceaccountservice.dto.account.ProfileDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.Profile;
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

import java.util.*;

@Service
public class ProfileServiceImpl extends BaseServiceImpl implements ProfileService {

    private static final int CARD_ID         = 0;
    private static final int CARD_NAME       = 1;
    private static final int CARD_ABOUT      = 2;
    private static final int CARD_BIRTH_YEAR = 3;
    private static final int CARD_DISTANCE   = 4;
    private static final int CARD_PHOTO_KEY  = 5;
    private static final int CARD_HEIGHT     = 6;

    private static final int PAGE_LIMIT = 15;

    private static final int maxDistance = 10000;
    private static final int minDistance = 1000;

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

//    @Override
//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
//    public ProfileDTO getProfile(UUID accountId, UUID identityToken) {
//        Account account = findValidAccount(accountId, identityToken);
//        return modelMapper.map(account, ProfileDTO.class);
//    }

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
        Profile profile = profileDAO.findById(accountId);

        if (profile == null) {
            profile = new Profile();
        }

//        Account account = findValidAccount(accountId, identityToken);
//
//        if (!account.isEnabled()) {
//            account.setName(name);
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(account.getBirth());
//
//            account.setBirthYear(calendar.get(Calendar.YEAR));
//            account.setBirth(birth);
//            account.setGender(gender);
//            account.setHeight(height);
//            account.setAbout(about);
//            account.setUpdatedAt(new Date());
//            account.setEnabled(true);
//        }
    }

//    @Override
//    @Transactional
//    public void saveAbout(UUID accountId, UUID identityToken, String about, Integer height) {
//        Account account = findValidAccount(accountId, identityToken);
//        account.setHeight(height);
//        account.setAbout(about);
//    }
//
//    @Override
//    @Transactional
//    public void saveLocation(UUID accountId, UUID identityToken, double latitude, double longitude, Date updatedAt) {
//        Account account = findValidAccount(accountId, identityToken);
//        saveLocation(account, latitude, longitude, updatedAt);
//    }
//
//    private void saveLocation(Account account, double latitude, double longitude, Date updatedAt) {
//        account.setLocation(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
//        account.setLocationUpdatedAt(updatedAt);
//    }
//
//    @Override
//    @Transactional
//    public PreRecommendDTO preRecommend(UUID accountId,
//                                        UUID identityToken,
//                                        Double latitude,
//                                        Double longitude,
//                                        Date locationUpdatedAt,
//                                        boolean reset) {
//        Account account = findValidAccount(accountId, identityToken);
//
//        PreRecommendDTO preRecommendDTO = new PreRecommendDTO();
//
//        int index = reset ? 0 : account.getIndex();
//        preRecommendDTO.setIndex(index);
//        index++;
//        account.setIndex(index);
//
//        if (latitude != null &&
//            longitude != null &&
//            locationUpdatedAt != null &&
//            locationUpdatedAt.after(account.getLocationUpdatedAt()))
//            saveLocation(account, latitude, longitude, locationUpdatedAt);
//
//        preRecommendDTO.setLocation(account.getLocation());
//        return preRecommendDTO;
//    }
//
//
//    // TEST 1. matches are mapped by matcher_id not matched_id
//    @Override
//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
//    public List<CardDTO> recommend(int distance, int minAge, int maxAge, boolean gender, Point location, int index) {
//        if (distance < minDistance || distance > maxDistance)
//            distance = maxDistance;
//
//        List<Object[]> accounts = accountDAO.findAllWithin(distance, minAge, maxAge, gender, PAGE_LIMIT,
//                                                           index * PAGE_LIMIT, location);
//
//        String previousId = "";
//        List<CardDTO> cardDTOs = new ArrayList<>();
//        CardDTO cardDTO = new CardDTO();
//
//        for (Object[] cAccount : accounts) {
//            String id = cAccount[CARD_ID].toString();
//            if (!previousId.equals(id)) {
//
//                cardDTOs.add(cardDTO);
//                previousId = id;
//
//                String name = cAccount[CARD_NAME].toString();
//                String about = cAccount[CARD_ABOUT].toString();
//                int birthYear = Integer.parseInt(cAccount[CARD_BIRTH_YEAR].toString());
//                int distanceBetween = (int) ((double) cAccount[CARD_DISTANCE]);
//                Integer height = (Integer) cAccount[CARD_HEIGHT];
//
//                cardDTO = new CardDTO(id, name, about, height, birthYear, distanceBetween);
//            }
//
//            if (cAccount[CARD_PHOTO_KEY] != null)
//                cardDTO.getPhotos().add(cAccount[CARD_PHOTO_KEY].toString());
//        }
//
//        cardDTOs.add(cardDTO);
//        cardDTOs.remove(0);
//        return cardDTOs;
//    }


}
