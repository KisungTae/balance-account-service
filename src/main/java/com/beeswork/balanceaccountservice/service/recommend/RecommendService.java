package com.beeswork.balanceaccountservice.service.recommend;

import com.beeswork.balanceaccountservice.dto.account.AccountProfileDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;

import java.util.List;
import java.util.UUID;

public interface RecommendService {

    List<AccountProfileDTO> accountsWithin(String accountId, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude)
    throws AccountNotFoundException;

    long swipe(String swiperId, String swiperEmail, String swipedId) throws AccountNotFoundException;
}
