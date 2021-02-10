package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dto.match.ListMatchDTO;
import com.beeswork.balanceaccountservice.projection.MatchProjection;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MatchService {


    ListMatchDTO listMatches(UUID accountId,
                             UUID identityToken,
                             Date matchFetchedAt,
                             Date accountFetchedAt,
                             Date chatMessageFetchedAt);

    void unmatch(UUID accountId, UUID identityToken, UUID unmatchedId);
}
