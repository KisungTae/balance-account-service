package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.projection.MatchProjection;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MatchDAO extends BaseDAO<Match> {


    List<MatchProjection> findAllAfter(UUID matcherId, Date fetchedAt);
    List<Match> findPairById(UUID matcherId, UUID matchedId);
    Match findWithAccounts(UUID matcherId, UUID matchedId, Long chatId);
}
