package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.projection.MatchProjection;

import java.util.Date;
import java.util.List;

public interface MatchService {


    List<MatchProjection> listMatches(String accountId, String identityToken, Date fetchedAt);
    void unmatch(String accountId, String identityToken, String unmatchedId);
}
