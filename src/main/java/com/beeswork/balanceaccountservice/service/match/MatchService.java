package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dto.match.CountMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.UnmatchDTO;

import java.util.UUID;

public interface MatchService {
    ListMatchesDTO fetchMatches(UUID swiperId, Long lastMatchId, int loadSize, MatchPageFilter matchPageFilter);
    ListMatchesDTO listMatches(UUID swiperId, int startPosition, int loadSize, MatchPageFilter matchPageFilter);
    void syncMatch(UUID swiperId, UUID chatId, long lastReadReceivedChatMessageId);
    UnmatchDTO unmatch(UUID swiperId, UUID swipedId);
    UnmatchDTO reportMatch(UUID reporterId, UUID reportedId, int reportReasonId, String description);
}
