package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dto.account.AccountProfileDTO;
import com.beeswork.balanceaccountservice.dto.match.BalanceDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.match.SwipeDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeBalancedExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;

import java.util.List;

public interface MatchService {

    List<AccountProfileDTO> recommend(String accountId, int distance, int minAge, int maxAge, boolean gender, double latitude, double longitude)
    throws AccountNotFoundException;

    BalanceDTO swipe(SwipeDTO swipeDTO)
    throws AccountNotFoundException, AccountInvalidException, SwipeBalancedExistsException,
           AccountShortOfPointException;

    MatchDTO balance(SwipeDTO swipeDTO) throws SwipeNotFoundException, AccountInvalidException, MatchExistsException;
}
