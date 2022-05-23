package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageReceiptDAO;
import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.SaveChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessageReceipt;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchUnmatchedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedDeletedException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl extends BaseServiceImpl implements ChatService {

    private final MatchDAO              matchDAO;
    private final ChatMessageDAO        chatMessageDAO;
    private final ChatMessageReceiptDAO chatMessageReceiptDAO;
    private final AccountDAO            accountDAO;
    private final ModelMapper           modelMapper;


    @Autowired
    public ChatServiceImpl(MatchDAO matchDAO,
                           ChatMessageDAO chatMessageDAO,
                           ChatMessageReceiptDAO chatMessageReceiptDAO,
                           AccountDAO accountDAO, ModelMapper modelMapper) {
        this.matchDAO = matchDAO;
        this.chatMessageDAO = chatMessageDAO;
        this.chatMessageReceiptDAO = chatMessageReceiptDAO;
        this.accountDAO = accountDAO;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ChatMessageDTO> fetchChatMessages(UUID senderId, UUID chatId, Long lastChatMessageId, int loadSize) {
        if (!matchDAO.existsBy(senderId, chatId)) {
            throw new MatchNotFoundException();
        }
        List<ChatMessage> chatMessages = chatMessageDAO.findAllBy(chatId, lastChatMessageId, loadSize);
        return convertToChatMessageDTO(senderId, chatMessages);
    }

    @Override
    public List<ChatMessageDTO> listChatMessages(UUID senderId, UUID chatId, UUID appToken, int startPosition, int loadSize) {
        if (!matchDAO.existsBy(senderId, chatId)) {
            throw new MatchNotFoundException();
        }
        List<ChatMessage> chatMessages = chatMessageDAO.findAllBy(senderId, chatId, appToken, startPosition, loadSize);
        return convertToChatMessageDTO(senderId, chatMessages);
    }

    @Override
    @Transactional
    public void syncChatMessages(UUID accountId, UUID chatId, UUID appToken, List<Long> chatMessageIds) {
        if (chatMessageIds.size() <= 0) {
            return;
        }
        Date now = new Date();
        List<ChatMessageReceipt> chatMessageReceipts = chatMessageReceiptDAO.findAllBy(accountId, chatId, chatMessageIds);
        Set<Long> chatMessageIdsSet = new HashSet<>(chatMessageIds);

        for (ChatMessageReceipt chatMessageReceipt : chatMessageReceipts) {
            chatMessageReceipt.setAppToken(appToken);
            chatMessageReceipt.setUpdatedAt(now);
            chatMessageIdsSet.remove(chatMessageReceipt.getChatMessageId());
        }

        for (Long chatMessageId : chatMessageIdsSet) {
            ChatMessageReceipt chatMessageReceipt = new ChatMessageReceipt(chatMessageId, accountId, chatId, appToken, now);
            chatMessageReceiptDAO.persist(chatMessageReceipt);
        }
    }


    private List<ChatMessageDTO> convertToChatMessageDTO(UUID senderId, List<ChatMessage> chatMessages) {
        List<ChatMessageDTO> chatMessageDTOs = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            ChatMessageDTO chatMessageDTO = modelMapper.map(chatMessage, ChatMessageDTO.class);
            if (!chatMessage.getSenderId().equals(senderId)) {
                chatMessageDTO.setTag(null);
            }
            chatMessageDTOs.add(chatMessageDTO);
        }
        return chatMessageDTOs;
    }


    @Override
    @Transactional
    public SaveChatMessageDTO saveChatMessage(ChatMessageDTO chatMessageDTO) {
        // NOTE 1. because account will be cached no need to query with join which does not go through second level cache
        SaveChatMessageDTO saveChatMessageDTO = new SaveChatMessageDTO();

        List<Match> matches = matchDAO.findAllBy(chatMessageDTO.getChatId(), true);
        if (matches.size() != 2) {
            saveChatMessageDTO.setError(MatchNotFoundException.CODE);
            return saveChatMessageDTO;
        }

        Match senderMatch = null;
        Match recipientMatch = null;
        for (Match match : matches) {
            if (match.getSwiperId().equals(chatMessageDTO.getSenderId())) {
                senderMatch = match;
            } else {
                recipientMatch = match;
            }
        }

        if (senderMatch == null || recipientMatch == null) {
            saveChatMessageDTO.setError(MatchNotFoundException.CODE);
            return saveChatMessageDTO;
        }

        if (senderMatch.isUnmatched() || recipientMatch.isUnmatched()) {
            saveChatMessageDTO.setError(MatchUnmatchedException.CODE);
            return saveChatMessageDTO;
        }

        ChatMessage chatMessage = chatMessageDAO.findBy(chatMessageDTO.getSenderId(), chatMessageDTO.getChatId(), chatMessageDTO.getTag());
        if (chatMessage == null) {
            Date now = new Date();
            chatMessage = modelMapper.map(chatMessageDTO, ChatMessage.class);
            chatMessage.setSender(senderMatch.getSwiper());
            chatMessage.setCreatedAt(now);
            chatMessage.setUpdatedAt(now);
            chatMessageDAO.persist(chatMessage);
        }

        if (senderMatch.getLastChatMessageId() == 0) {
            saveChatMessageDTO.setFirstMessage(true);
        }

        if (chatMessage.getId() > senderMatch.getLastChatMessageId()) {
            senderMatch.setLastChatMessageId(chatMessage.getId());
            senderMatch.setLastChatMessageBody(chatMessage.getBody());
            senderMatch.setLastChatMessageCreatedAt(chatMessage.getCreatedAt());
        }

        if (chatMessage.getId() > recipientMatch.getLastChatMessageId()) {
            recipientMatch.setLastChatMessageId(chatMessage.getId());
            recipientMatch.setLastChatMessageBody(chatMessage.getBody());
            recipientMatch.setLastChatMessageCreatedAt(chatMessage.getCreatedAt());
        }

        if (chatMessage.getId() > recipientMatch.getLastReceivedChatMessageId()) {
            recipientMatch.setLastReceivedChatMessageId(chatMessage.getId());
        }

        saveChatMessageDTO.setId(chatMessage.getId());
        saveChatMessageDTO.setCreatedAt(chatMessage.getCreatedAt());
        saveChatMessageDTO.setRecipientId(senderMatch.getSwipedId());
        return saveChatMessageDTO;
    }

}
