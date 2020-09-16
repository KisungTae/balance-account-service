package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;

import java.util.UUID;

public interface MatchDAO extends BaseDAO<Match> {

    boolean existsById(MatchId matchId);
}
