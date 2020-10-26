package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.match.UnmatchDTO;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.match.MatchId;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class MatchServiceImpl extends BaseServiceImpl implements MatchService, MatchInterService {

    private final AccountInterService accountInterService;

    private final MatchDAO   matchDAO;

    @Autowired
    public MatchServiceImpl(ModelMapper modelMapper, AccountInterService accountInterService, MatchDAO matchDAO) {
        super(modelMapper);
        this.accountInterService = accountInterService;
        this.matchDAO = matchDAO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<MatchProjection> listMatches(String matcherId, String email, Date fetchedAt)
    throws AccountInvalidException {

        accountInterService.checkIfValid(UUID.fromString(matcherId), email);
        return matchDAO.findAllAfter(UUID.fromString(matcherId), fetchedAt);
    }

    @Override
    @Transactional
    public void unmatch(UnmatchDTO unmatchDTO) throws AccountInvalidException {

        accountInterService.checkIfValid(UUID.fromString(unmatchDTO.getUnmatcherId()), unmatchDTO.getUnmatcherEmail());

        List<Match> matches = matchDAO.findPairById(UUID.fromString(unmatchDTO.getUnmatcherId()),
                                                    UUID.fromString(unmatchDTO.getUnmatchedId()));

        Date date = new Date();
        for (Match match : matches) {
            match.setUnmatched(true);
            match.setUpdatedAt(date);
            if (match.getMatcherId().toString().equals(unmatchDTO.getUnmatcherId()))
                match.setUnmatcher(true);
            matchDAO.persist(match);
        }

    }

    @Override
    public boolean existsById(UUID matcherId, UUID matchedId) {
        return matchDAO.existsById(new MatchId(matcherId, matchedId));
    }
}
