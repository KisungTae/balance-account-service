package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dao.chatmessage.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {

    private final MatchDAO matchDAO;
    private final ChatMessageDAO chatMessageDAO;

    @Autowired
    public ChatServiceImpl(MatchDAO matchDAO,
                           ChatMessageDAO chatMessageDAO) {
        this.matchDAO = matchDAO;
        this.chatMessageDAO = chatMessageDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public void validateChat(String accountId, String identityToken, String recipientId, String chatId) {
        validateChat(matchDAO.findById(UUID.fromString(accountId), UUID.fromString(recipientId)),
                     UUID.fromString(identityToken),
                     Long.valueOf(chatId));
    }

    private void validateChat(Match match, UUID identityToken, Long chatId) {
        if (match == null)
            throw new MatchNotFoundException();
        if (match.isUnmatched())
            throw new MatchUnmatchedException();
        if (!match.getChatId().equals(chatId))
            throw new MatchNotFoundException();

        Account matcher = match.getMatcher();
        Account matched = match.getMatched();

        if (matcher == null || !matcher.getIdentityToken().equals(identityToken))
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
    }

    @Override
    @Transactional
    public Long validateAndSaveMessage(ChatMessageDTO chatMessageDTO, String identityToken, String messageId) {
        // NOTE 1. because account will be cached no need to query with join which does not go through second level cache
        Match match = matchDAO.findById(UUID.fromString(chatMessageDTO.getAccountId()), UUID.fromString(chatMessageDTO.getRecipientId()));
        validateChat(match, UUID.fromString(identityToken), Long.valueOf(chatMessageDTO.getChatId()));
        ChatMessage chatMessage = new ChatMessage(match.getChat(),
                                                  match.getMatcher(),
                                                  match.getMatched(),
                                                  Long.valueOf(messageId),
                                                  chatMessageDTO.getMessage(),
                                                  chatMessageDTO.getCreatedAt());
        chatMessageDAO.persist(chatMessage);
        return chatMessage.getId();
    }


}
