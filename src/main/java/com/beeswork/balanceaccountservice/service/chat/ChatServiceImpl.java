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
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchUnmatchedException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        if (lastChatMessageId == null) {
            lastChatMessageId = Long.MAX_VALUE;
        }
        List<ChatMessage> chatMessages = chatMessageDAO.findAllBy(chatId, lastChatMessageId, loadSize);
        return convertToChatMessageDTO(senderId, chatMessages);
    }

    @Override
    public List<ChatMessageDTO> listChatMessages(UUID senderId, UUID chatId, UUID appToken, int startPosition, int loadSize) {
        List<ChatMessage> chatMessages = chatMessageDAO.findAllBy(senderId, chatId, appToken, startPosition, loadSize);
        return convertToChatMessageDTO(senderId, chatMessages);
    }

    @Override
    @Transactional
    public void syncChatMessages(UUID accountId, UUID chatId, UUID appToken, List<Long> chatMessageIds) {
        Account account = accountDAO.findById(accountId, false);
        if (account == null) {
            return;
        }
        Date now = new Date();
        for (long chatMessageId : chatMessageIds) {
            ChatMessageReceipt chatMessageReceipt = chatMessageReceiptDAO.findBy(accountId, chatId, chatMessageId);
            if (chatMessageReceipt == null) {
                ChatMessage chatMessage = chatMessageDAO.findBy(chatId, chatMessageId);
                if (chatMessage == null) {
                    continue;
                }
                ChatMessageReceipt newChatMessageReceipt = new ChatMessageReceipt(chatMessage, account, appToken, now, now);
                chatMessageReceiptDAO.persist(newChatMessageReceipt);
            } else {
                chatMessageReceipt.setAppToken(appToken);
                chatMessageReceipt.setUpdatedAt(now);
            }
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
        Match match = matchDAO.findBy(chatMessageDTO.getSenderId(), chatMessageDTO.getRecipientId(), true);
//        if (match == null || match.getSwiped() == null || match.getChat() == null || match.getChatId() != chatMessageDTO.getChatId()) {
//            saveChatMessageDTO.setError(MatchNotFoundException.CODE);
//            return saveChatMessageDTO;
//        }
//      TODO: return swipeDId as well because android sends chatMessage wihtout swipedId
        if (match.isUnmatched() || match.getSwiped().isDeleted() || match.getSwiped().isBlocked()) {
            saveChatMessageDTO.setError(MatchUnmatchedException.CODE);
            return saveChatMessageDTO;
        }

//        SentChatMessage sentChatMessage = sentChatMessageDAO.findById(chatMessageDTO.getId());
//        if (sentChatMessage == null) {
//            Date now = new Date();
//            ChatMessage chatMessage = new ChatMessage(match.getChat(), match.getSwiped(), chatMessageDTO.getBody(), now);
//            sentChatMessage = new SentChatMessage(chatMessage, match.getSwiper(), now);
//            chatMessageDAO.persist(chatMessage);
//            sentChatMessageDAO.persist(sentChatMessage);
//        }

//        if (!match.isActive()) {
//            activateMatch(chatMessageDTO.getAccountId(), chatMessageDTO.getRecipientId());
//        }
//        saveChatMessageDTO.setCreatedAt(sentChatMessage.getCreatedAt());
        return saveChatMessageDTO;
    }

    private void activateMatch(UUID senderId, UUID recipientId) {
        UUID swiperId = senderId.compareTo(recipientId) > 0 ? senderId : recipientId;
        UUID swipedId = senderId.compareTo(recipientId) > 0 ? recipientId : senderId;
        Match swiperMatch = matchDAO.findBy(swiperId, swipedId, true);
        Match swipedMatch = matchDAO.findBy(swipedId, swiperId, true);
//        swiperMatch.setActive(true);
//        swipedMatch.setActive(true);
    }

    @Override
    @Transactional
    public void fetchedChatMessage(UUID accountId, UUID chatMessageId) {
        Match match = matchDAO.findBy(accountId, chatMessageId, false);

        Match match1 = matchDAO.findBy(accountId, chatMessageId, true);
        match1.setUpdatedAt(new Date());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        SentChatMessage sentChatMessage = sentChatMessageDAO.findById(chatMessageId);
//        if (sentChatMessage == null || !sentChatMessage.getAccountId().equals(accountId)) {
//            throw new ChatMessageNotFoundException();
//        }
//        sentChatMessage.setFetched(true);
//        sentChatMessage.setUpdatedAt(new Date());
    }

    @Override
    @Transactional
    public void receivedChatMessage(UUID accountId, UUID chatMessageId) {
        Match match1 = matchDAO.findBy(accountId, chatMessageId, true);
        match1.setUpdatedAt(new Date());
//        ChatMessage chatMessage = chatMessageDAO.findById(chatMessageId);
//        if (chatMessage == null || !chatMessage.getRecipientId().equals(accountId)) {
//            throw new ChatMessageNotFoundException();
//        }
//        chatMessage.setReceived(true);
//        chatMessage.setUpdatedAt(new Date());
    }


}
