package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountDeletedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchUnmatchedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedBlockedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedDeletedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {

    private final MatchDAO matchDAO;

    @Autowired
    public ChatServiceImpl(MatchDAO matchDAO) {
        this.matchDAO = matchDAO;
    }

    @Override
    public String checkIfValidChat(String accountId, String identityToken, String chatId) {
        Match match = matchDAO.findWithAccounts(UUID.fromString(accountId), Long.valueOf(chatId));

        if (match == null)
            throw new MatchNotFoundException();
        if (match.isUnmatched())
            throw new MatchUnmatchedException();

        Account matcher = match.getMatcher();
        Account matched = match.getMatched();

        if (matcher == null || !matcher.getIdentityToken().equals(UUID.fromString(identityToken)))
            throw new AccountNotFoundException();
        if (matcher.isBlocked())
            throw new AccountBlockedException();
        if (matcher.isDeleted())
            throw new AccountDeletedException();

        if (matched == null)
            throw new SwipedNotFoundException();
        if (matched.isBlocked())
            throw new SwipedBlockedException();
        if (matched.isDeleted())
            throw new SwipedDeletedException();

        return matched.getId().toString();
    }
}
