package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.projection.MatchProjection;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MatchService {


    List<MatchProjection> listMatches(UUID accountId, UUID identityToken, Date fetchedAt);
    void unmatch(UUID accountId, UUID identityToken, UUID unmatchedId);
}
