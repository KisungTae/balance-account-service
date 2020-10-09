package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dto.match.UnmatchDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.projection.MatchProjection;

import java.util.List;

public interface MatchService {


    List<MatchProjection> listMatches(String matcherId);
    void unmatch(UnmatchDTO unmatchDTO) throws AccountInvalidException;
}
