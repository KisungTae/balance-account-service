package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.chat.SentChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.SentChatMessage;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ChatServiceImpl extends BaseServiceImpl implements ChatService {

    private final MatchDAO matchDAO;
    private final ChatMessageDAO chatMessageDAO;
    private final AccountDAO accountDAO;
    private final SentChatMessageDAO sentChatMessageDAO;


    @Autowired
    public ChatServiceImpl(MatchDAO matchDAO,
                           ChatMessageDAO chatMessageDAO,
                           AccountDAO accountDAO,
                           SentChatMessageDAO sentChatMessageDAO) {
        this.matchDAO = matchDAO;
        this.chatMessageDAO = chatMessageDAO;
        this.accountDAO = accountDAO;
        this.sentChatMessageDAO = sentChatMessageDAO;
    }

    @Override
    @Transactional
    public Long saveChatMessage(UUID accountId, UUID identityToken, long chatId, UUID recipientId, long key, String body, Date createdAt) {
        // NOTE 1. because account will be cached no need to query with join which does not go through second level cache
        Match match = matchDAO.findById(accountId, recipientId);
        if (match == null) return null;
        validateAccount(match.getSwiper(), identityToken);
        Account swiped = match.getSwiped();
        Chat chat = match.getChat();
        if (swiped == null || chat == null || chat.getId() != chatId) return null;
        if (match.isUnmatched() || match.getSwiped().isDeleted()) return StompHeader.UNMATCHED_RECEIPT_ID;

        SentChatMessage sentChatMessage = sentChatMessageDAO.findByKey(accountId, key);
        if (sentChatMessage == null) {
            ChatMessage chatMessage = new ChatMessage(chat, swiped, body, createdAt);
            sentChatMessage = new SentChatMessage(chatMessage, match.getSwiper(), key, createdAt);
            chatMessageDAO.persist(chatMessage);
            sentChatMessageDAO.persist(sentChatMessage);
        }

        if (!match.isActive()) {
            match.setActive(true);
            matchDAO.persist(match);
        }
        return sentChatMessage.getChatMessageId();
    }

    @Override
    @Transactional
    public void syncChatMessages(UUID accountId,
                                 UUID identityToken,
                                 List<Long> sentChatMessageIds,
                                 List<Long> receivedChatMessageIds) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        List<ChatMessage> receivedChatMessages = chatMessageDAO.findAllIn(receivedChatMessageIds);
        Date now = new Date();
        for (ChatMessage chatMessage : receivedChatMessages) {
            chatMessage.setReceived(true);
            chatMessage.setUpdatedAt(now);
        }
        List<SentChatMessage> sentChatMessages = sentChatMessageDAO.findAllIn(sentChatMessageIds);
        for (SentChatMessage sentChatMessage : sentChatMessages) {
            sentChatMessage.setFetched(true);
            sentChatMessage.setUpdatedAt(now);
        }
    }



    //    @Override
//    @Transactional
//    public void receivedChatMessage(UUID accountId, UUID identityToken, Long chatMessageId) {
//        validateAccount(accountDAO.findById(accountId), identityToken);
//        ChatMessage chatMessage = chatMessageDAO.findById(chatMessageId);
//        chatMessage.setReceived(true);
//    }
//
//    @Override
//    @Transactional
//    public void fetchedChatMessage(UUID accountId, UUID identityToken, Long chatMessageId) {
//        validateAccount(accountDAO.findById(accountId), identityToken);
//        SentChatMessage sentChatMessage = sentChatMessageDAO.findByKey(accountId, chatMessageId);
//        sentChatMessage.setFetched(true);
//    }
}
