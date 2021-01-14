package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.match.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {

    private final MatchDAO matchDAO;

    @Autowired
    public ChatServiceImpl(MatchDAO matchDAO) {
        this.matchDAO = matchDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public void test() {
        matchDAO.test();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public void validateChat(String accountId, String identityToken, String recipientId, String chatId) {
//        validateChat(matchDAO.findWithAccounts(UUID.fromString(accountId),
//                                               UUID.fromString(recipientId),
//                                               Long.valueOf(chatId)),
//                     identityToken);
    }

    private void validateChat(Match match, String identityToken) {
//        if (match == null)
//            throw new MatchNotFoundException();
//        if (match.isUnmatched())
//            throw new MatchUnmatchedException();
//
//        Account matcher = match.getMatcher();
//        Account matched = match.getMatched();
//
//        if (matcher == null || !matcher.getIdentityToken().equals(UUID.fromString(identityToken)))
//            throw new AccountNotFoundException();
//        if (matcher.isBlocked())
//            throw new AccountBlockedException();
//        if (matcher.isDeleted())
//            throw new AccountDeletedException();
//
//        if (matched == null)
//            throw new SwipedNotFoundException();
//        if (matched.isBlocked())
//            throw new SwipedBlockedException();
//        if (matched.isDeleted())
//            throw new SwipedDeletedException();
    }

    @Override
    public void validateAndSaveMessage(String accountId,
                                       String identityToken,
                                       String matchedId,
                                       String chatId,
                                       String message,
                                       Date createdAt) {
        Match match = matchDAO.findWithAccounts(UUID.fromString(accountId),
                                                UUID.fromString(matchedId),
                                                Long.valueOf(chatId));
        validateChat(match, identityToken);
        

    }
}
