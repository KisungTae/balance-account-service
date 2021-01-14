package com.beeswork.balanceaccountservice.service.match;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
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
    public List<MatchProjection> listMatches(String accountId, String identityToken, Date fetchedAt) {
        UUID accountUUId = UUID.fromString(accountId);
        Account account = accountDAO.findBy(accountUUId, UUID.fromString(identityToken));
        checkIfAccountValid(account);
        return matchDAO.findAllAfter(accountUUId, fetchedAt);
    }

    @Override
    @Transactional
    public void unmatch(String accountId, String identityToken, String unmatchedId) {
        UUID unmatcherUUID = UUID.fromString(accountId);
        List<Match> matches = matchDAO.findPairById(unmatcherUUID, UUID.fromString(unmatchedId));
        Date date = new Date();

        for (Match match : matches) {
            if (match.getMatcherId().equals(unmatcherUUID)) {
                if (!match.getMatcher().getIdentityToken().equals(UUID.fromString(identityToken)))
                    throw new AccountNotFoundException();
                match.setUnmatcher(true);
            }
            match.setUnmatched(true);
            match.setUpdatedAt(date);
        }
    }

}
