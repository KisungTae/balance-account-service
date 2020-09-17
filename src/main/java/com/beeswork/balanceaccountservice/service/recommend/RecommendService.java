package com.beeswork.balanceaccountservice.service.recommend;

import com.beeswork.balanceaccountservice.dto.account.AccountProfileDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;

import java.util.List;

public interface RecommendService {

    List<AccountProfileDTO> recommend(String accountId, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude)
    throws AccountNotFoundException;
}
