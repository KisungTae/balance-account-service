package com.beeswork.balanceaccountservice.service.recommend;

import com.beeswork.balanceaccountservice.constant.AppConstant;
import com.beeswork.balanceaccountservice.constant.ColumnIndex;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.recommend.RecommendDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.service.account.AccountInterService;
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

    private final AccountInterService accountInterService;

    @Autowired
    public RecommendServiceImpl(AccountInterService accountInterService) {
        this.accountInterService = accountInterService;
    }

    // TEST 1. matches are mapped by matcher_id not matched_id
    @Override
    @Transactional(propagation = Propagation.REQUIRED,
                   isolation = Isolation.READ_COMMITTED,
                   readOnly = true)
    public List<CardDTO> recommend(RecommendDTO recommendDTO) {

        List<Object[]> accounts = accountInterService.findAllWithin(UUID.fromString(recommendDTO.getAccountId()),
                                                                    recommendDTO.getEmail(),
                                                                    recommendDTO.getDistance(),
                                                                    recommendDTO.getMinAge(),
                                                                    recommendDTO.getMaxAge(),
                                                                    recommendDTO.isGender(),
                                                                    recommendDTO.getLatitude(),
                                                                    recommendDTO.getLongitude());

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
