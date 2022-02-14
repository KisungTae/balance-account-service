package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MatchDAO extends BaseDAO<Match> {
    Match findBy(UUID swiperId, UUID swipedId, boolean writeLock);
    List<MatchDTO> findAllAfter(UUID swipedId, Date matchFetchedAt);
}
