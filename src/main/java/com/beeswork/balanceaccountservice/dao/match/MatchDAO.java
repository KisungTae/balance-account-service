package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MatchDAO extends BaseDAO<Match> {

    List<MatchDTO> findAllBy(UUID swiperId, UUID lastSwipedId, int loadSize, MatchPageFilter matchPageFilter);
    List<MatchDTO> findAllBy(UUID swiperId, int startPosition, int loadSize, MatchPageFilter matchPageFilter);
    Match findBy(UUID swiperId, UUID swipedId, boolean writeLock);
    long countMatchesBy(UUID swiperId);
}
