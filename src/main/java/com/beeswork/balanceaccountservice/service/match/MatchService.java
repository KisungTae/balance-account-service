package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;

import java.util.List;
import java.util.UUID;

public interface MatchService {
    List<MatchDTO> fetchMatches(UUID swiperId, UUID lastSwipedId, int loadSize, MatchPageFilter matchPageFilter);
    List<MatchDTO> listMatches(UUID swiperId, int startPosition, int loadSize, MatchPageFilter matchPageFilter);
    void unmatch(UUID swiperId, UUID swipedId);
    void reportMatch(UUID reporterId, UUID reportedId, int reportReasonId, String description);

}
