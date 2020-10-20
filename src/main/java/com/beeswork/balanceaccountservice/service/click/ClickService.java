package com.beeswork.balanceaccountservice.service.click;

import com.beeswork.balanceaccountservice.dto.click.ClickDTO;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;

import java.util.Date;
import java.util.List;

public interface ClickService {

    List<FCMNotificationDTO> click(ClickDTO clickDTO) throws SwipeNotFoundException, AccountInvalidException, AccountNotFoundException,
                                                             AccountBlockedException, SwipeClickedExistsException;
    List<ClickedProjection> listClicked(String clickedId, String email, Date fetchedAt) throws AccountInvalidException,
                                                                                               AccountNotFoundException;

}
