package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;

import java.util.List;
import java.util.UUID;

public interface MatchDAO extends BaseDAO<Match> {

    List<MatchDTO> findAll(UUID swiperId, Long lastMatchId, int loadSize, MatchPageFilter matchPageFilter);
    List<MatchDTO> findAll(UUID swiperId, int startPosition, int loadSize, MatchPageFilter matchPageFilter);
    Match find(UUID swiperId, UUID swipedId, boolean writeLock);
    long countMatches(UUID swiperId);
    long countUnreadChats(UUID swiperId);
    boolean exists(UUID swiperId, UUID chatId);
    List<Match> findAll(UUID chatId, boolean writeLock);
}
