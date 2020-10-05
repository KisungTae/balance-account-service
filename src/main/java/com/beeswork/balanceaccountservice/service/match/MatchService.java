package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dto.match.ClickDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;

public interface MatchService {


    ClickDTO click(SwipeDTO swipeDTO) throws SwipeNotFoundException, AccountInvalidException, MatchExistsException;
}
