package com.beeswork.balanceaccountservice.service.swipe;

import com.beeswork.balanceaccountservice.dto.match.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeListDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeBalancedExistsException;

public interface SwipeService {

    BalanceGameDTO swipe(SwipeDTO swipeDTO)
    throws AccountNotFoundException, AccountInvalidException, SwipeBalancedExistsException,
           AccountShortOfPointException;

    SwipeListDTO listSwipes(String accountId);
}
