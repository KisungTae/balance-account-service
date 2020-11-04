package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Match;
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
public class MatchServiceImpl extends BaseServiceImpl implements MatchService {

    private final AccountDAO accountDAO;
    private final MatchDAO   matchDAO;

    @Autowired
    public MatchServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, MatchDAO matchDAO) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.matchDAO = matchDAO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<MatchProjection> listMatches(String matcherId, String email, Date fetchedAt) {

        UUID matcherUUID = UUID.fromString(matcherId);
        Account account = accountDAO.findBy(matcherUUID, email);
        checkIfAccountValid(account);
        return matchDAO.findAllAfter(matcherUUID, fetchedAt);
    }

    @Override
    @Transactional
    public void unmatch(String unmatcherId, String unmatcherEmail, String unmatchedId) {

        UUID unmatcherUUID = UUID.fromString(unmatchedId);
        if (!accountDAO.existsBy(unmatcherUUID, unmatcherEmail, false))
            throw new AccountInvalidException();

        List<Match> matches = matchDAO.findPairById(UUID.fromString(unmatcherId), UUID.fromString(unmatchedId));

        Date date = new Date();
        for (Match match : matches) {
            match.setUnmatched(true);
            match.setUpdatedAt(date);
            if (match.getMatcherId().equals(unmatcherUUID))
                match.setUnmatcher(true);
        }
    }

}
