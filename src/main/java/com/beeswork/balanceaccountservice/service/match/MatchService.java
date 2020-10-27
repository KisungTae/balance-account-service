package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.projection.MatchProjection;

import java.util.Date;
import java.util.List;

public interface MatchService {


    List<MatchProjection> listMatches(String matcherId, String email, Date fetchedAt) throws AccountInvalidException;
    void unmatch(String unmatcherId, String unmatcherEmail, String unmatchedId) throws AccountInvalidException;
}
