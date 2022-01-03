package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.chat.SentChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.ListChatMessagesDTO;
import com.beeswork.balanceaccountservice.dto.chat.SaveChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.SentChatMessage;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.exception.chat.ChatMessageNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchUnmatchedException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ChatServiceImpl extends BaseServiceImpl implements ChatService {

    private final MatchDAO           matchDAO;
    private final ChatMessageDAO     chatMessageDAO;
    private final AccountDAO         accountDAO;
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
    public SaveChatMessageDTO saveChatMessage(ChatMessageDTO chatMessageDTO) {
        // NOTE 1. because account will be cached no need to query with join which does not go through second level cache

        SaveChatMessageDTO saveChatMessageDTO = new SaveChatMessageDTO();
        Match match = matchDAO.findById(chatMessageDTO.getAccountId(), chatMessageDTO.getRecipientId());
        if (match == null || match.getSwiped() == null || match.getChat() == null || match.getChatId() != chatMessageDTO.getChatId()) {
            saveChatMessageDTO.setError(MatchNotFoundException.CODE);
            return saveChatMessageDTO;
        }

        if (match.isUnmatched() || match.getSwiped().isDeleted() || match.getSwiped().isBlocked()) {
            saveChatMessageDTO.setError(MatchUnmatchedException.CODE);
            return saveChatMessageDTO;
        }

        SentChatMessage sentChatMessage = sentChatMessageDAO.findById(chatMessageDTO.getId());
        if (sentChatMessage == null) {
            Date now = new Date();
            ChatMessage chatMessage = new ChatMessage(match.getChat(), match.getSwiped(), chatMessageDTO.getBody(), now);
            sentChatMessage = new SentChatMessage(chatMessage, match.getSwiper(), now);
            chatMessageDAO.persist(chatMessage);
            sentChatMessageDAO.persist(sentChatMessage);
        }

        if (!match.isActive()) {
            match.setActive(true);
            matchDAO.persist(match);
        }
        saveChatMessageDTO.setCreatedAt(sentChatMessage.getCreatedAt());
        saveChatMessageDTO.setError("common error");
        return saveChatMessageDTO;
    }

    @Override
    @Transactional
    public void syncChatMessages(List<UUID> sentChatMessageIds, List<UUID> receivedChatMessageIds) {
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

    @Override
    public ListChatMessagesDTO listChatMessages(UUID accountId) {
        ListChatMessagesDTO listChatMessagesDTO = new ListChatMessagesDTO();
        listChatMessagesDTO.setReceivedChatMessageDTOs(chatMessageDAO.findAllUnreceived(accountId));
        listChatMessagesDTO.setSentChatMessageDTOs(sentChatMessageDAO.findAllUnfetched(accountId));
        return listChatMessagesDTO;
    }

    @Override
    @Transactional
    public void fetchedChatMessage(UUID accountId, UUID chatMessageId) {
        SentChatMessage sentChatMessage = sentChatMessageDAO.findById(chatMessageId);
        if (sentChatMessage == null || !sentChatMessage.getAccountId().equals(accountId)) {
            throw new ChatMessageNotFoundException();
        }
        sentChatMessage.setFetched(true);
        sentChatMessage.setUpdatedAt(new Date());
    }

    @Override
    @Transactional
    public void receivedChatMessage(UUID accountId, UUID chatMessageId) {
        ChatMessage chatMessage = chatMessageDAO.findById(chatMessageId);
        if (chatMessage == null || !chatMessage.getRecipientId().equals(accountId)) {
            throw new ChatMessageNotFoundException();
        }
        chatMessage.setReceived(true);
        chatMessage.setUpdatedAt(new Date());
    }


}
