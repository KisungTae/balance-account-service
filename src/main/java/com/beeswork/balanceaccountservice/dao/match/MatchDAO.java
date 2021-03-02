package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.querydsl.core.Tuple;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MatchDAO extends BaseDAO<Match> {
    List<MatchDTO> findAllAfter(UUID matcherId, Date matchFetchedAt);
    Match findById(UUID matcherId, UUID matchedId);
}
