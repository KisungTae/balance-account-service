package com.beeswork.balanceaccountservice.service.recommend;

import com.beeswork.balanceaccountservice.constant.ColumnIndex;
import com.beeswork.balanceaccountservice.constant.QueryParameter;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.account.AccountProfileDTO;
import com.beeswork.balanceaccountservice.dto.account.PhotoDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Swipe;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
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
public class RecommendServiceImpl extends BaseServiceImpl implements RecommendService {

    private final AccountDAO accountDAO;

    private final GeometryFactory geometryFactory;

    @Autowired
    public RecommendServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, GeometryFactory geometryFactory) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.geometryFactory = geometryFactory;
    }

    // TEST 1. matches are mapped by matcher_id not matched_id
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<AccountProfileDTO> accountsWithin(String accountId, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude)
    throws AccountNotFoundException {

        UUID accountUUID = UUID.fromString(accountId);

        Account account = accountDAO.findById(accountUUID);
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        location.setSRID(QueryParameter.SRID);

        List<Object[]> accounts = accountDAO.findAllWithin(accountUUID, distance, minAge, maxAge, gender, QueryParameter.LIMIT,
                                                           QueryParameter.LIMIT * account.getIndex(), location);

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

    public long swipe(String swiperId, String swiperEmail, String swipedId) throws AccountNotFoundException {
        Account swiper = accountDAO.findById(UUID.fromString(swiperId));
        Account swiped = accountDAO.findById(UUID.fromString(swipedId));

        Swipe swipe = new Swipe(swiper, swiped, false, new Date());
        swiper.getSwipes().add(swipe);
        accountDAO.persist(swiper);

        // subtract point

        // check balanced swipe exists



        return swipe.getId();
    }


}
