package com.beeswork.balanceaccountservice.service.recommend;

import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.constant.ColumnIndex;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendServiceImpl implements RecommendService {

    private final AccountDAO accountDAO;
    private final GeometryFactory geometryFactory;

    @Autowired
    public RecommendServiceImpl(AccountDAO accountDAO, GeometryFactory geometryFactory) {
        this.accountDAO = accountDAO;
        this.geometryFactory = geometryFactory;
    }

    // TEST 1. matches are mapped by matcher_id not matched_id
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<CardDTO> recommend(String accountId, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude)
    throws AccountNotFoundException {

        UUID accountUUID = UUID.fromString(accountId);

        Account account = accountDAO.findById(accountUUID);
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        location.setSRID(AppConstant.SRID);

        List<Object[]> accounts = accountDAO.findAllWithin(accountUUID, distance, minAge, maxAge, gender, AppConstant.LIMIT,
                                                           AppConstant.LIMIT * account.getIndex(), location);

        String previousId = "";
        List<CardDTO> cardDTOs = new ArrayList<>();
        CardDTO cardDTO = new CardDTO();

        for (Object[] cAccount : accounts) {
            String id = cAccount[ColumnIndex.ACCOUNT_PROFILE_ID].toString();
            if (!previousId.equals(id)) {

                cardDTOs.add(cardDTO);
                previousId = id;

                String name = cAccount[ColumnIndex.ACCOUNT_PROFILE_NAME].toString();
                String about = cAccount[ColumnIndex.ACCOUNT_PROFILE_ABOUT].toString();
                int birthYear = Integer.parseInt(cAccount[ColumnIndex.ACCOUNT_PROFILE_BIRTH_YEAR].toString());
                int distanceBetween = (int) ((double) cAccount[ColumnIndex.ACCOUNT_PROFILE_DISTANCE]);

                cardDTO = new CardDTO(id, name, about, birthYear, distanceBetween);
            }
            cardDTO.getPhotos().add(cAccount[ColumnIndex.ACCOUNT_PROFILE_PHOTO_KEY].toString());
        }

        cardDTOs.add(cardDTO);
        cardDTOs.remove(0);
        return cardDTOs;
    }
}
