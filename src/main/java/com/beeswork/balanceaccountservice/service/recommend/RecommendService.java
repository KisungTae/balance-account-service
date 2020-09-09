package com.beeswork.balanceaccountservice.service.recommend;

import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;

import java.util.List;
import java.util.UUID;

public interface RecommendService {

    List<Account> accountsByLocation(UUID accountId, int distance, int minAge, int maxAge, boolean showMe) throws AccountNotFoundException;
}
