package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.account.AccountSimpleProfileDTO;
import com.beeswork.balanceaccountservice.dto.match.BalanceDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeBalancedExistsException;

import java.util.List;

public interface SwipeService {

    BalanceDTO swipe(SwipeDTO swipeDTO)
    throws AccountNotFoundException, AccountInvalidException, SwipeBalancedExistsException,
           AccountShortOfPointException;

    List<AccountSimpleProfileDTO> listSwipes(String accountId);
}
