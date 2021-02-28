package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.chat.ChatMessageDAO;
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
public class ChatServiceImpl extends BaseServiceImpl implements ChatService {

    private final MatchDAO       matchDAO;
    private final ChatMessageDAO chatMessageDAO;
    private final AccountDAO     accountDAO;

    @Autowired
    public ChatServiceImpl(MatchDAO matchDAO,
                           ChatMessageDAO chatMessageDAO,
                           ModelMapper modelMapper,
                           AccountDAO accountDAO) {
        super(modelMapper);
        this.matchDAO = matchDAO;
        this.chatMessageDAO = chatMessageDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public void validateChat(String accountId, String identityToken, String recipientId, String chatId) {
        validateChat(matchDAO.findById(UUID.fromString(accountId), UUID.fromString(recipientId)),
                     UUID.fromString(identityToken),
                     Long.valueOf(chatId));
    }

    private void validateChat(Match match, UUID identityToken, long chatId) {
        if (match == null)
            throw new MatchNotFoundException();
        if (match.isUnmatched())
            throw new MatchUnmatchedException();
        if (match.getChatId() != chatId)
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
    public ChatMessageDTO saveChatMessage(UUID accountId,
                                          UUID identityToken,
                                          UUID recipientId,
                                          long chatId,
                                          long messageId,
                                          String body,
                                          Date createdAt) {
        // NOTE 1. because account will be cached no need to query with join which does not go through second level cache
        Match match = matchDAO.findById(accountId, recipientId);
//        validateChat(match, identityToken, chatId);

        ChatMessage chatMessage = chatMessageDAO.findByMessageId(messageId);
        if (chatMessage == null) {
            chatMessage = new ChatMessage(match.getChat(),
                                          match.getMatcher(),
                                          match.getMatched(),
                                          messageId,
                                          body,
                                          createdAt);
            chatMessageDAO.persist(chatMessage);
        }

        if (!match.isActive()) {
            match.setActive(true);
            matchDAO.persist(match);
        }
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setCreatedAt(chatMessage.getCreatedAt());
        return chatMessageDTO;
    }

    @Override
    @Transactional
    public void receivedChatMessage(UUID accountId, UUID identityToken, Long chatMessageId) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        ChatMessage chatMessage = chatMessageDAO.findByIdWithLock(chatMessageId);
        chatMessage.setReceived(true);
    }

    @Override
    @Transactional
    public void fetchedChatMessage(UUID accountId, UUID identityToken, Long chatMessageId) {
        validateAccount(accountDAO.findById(accountId), identityToken);
        ChatMessage chatMessage = chatMessageDAO.findByIdWithLock(chatMessageId);
        chatMessage.setFetched(true);
    }

    @Override
    @Transactional
    public void syncChatMessages(UUID accountId, UUID identityToken, List<Long> chatMessageIds) {
//        checkIfAccountValid(accountDAO.findById(accountId), identityToken);
        for (ChatMessage chatMessage : chatMessageDAO.findAllInWithLock(chatMessageIds)) {
            if (chatMessage.getAccountId().equals(accountId)) chatMessage.setFetched(true);
            else chatMessage.setReceived(true);
        }
    }


//    @Override
//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
//    public List<ChatMessageDTO> listChatMessages(UUID accountId,
//                                                 UUID identityToken,
//                                                 UUID recipientId,
//                                                 Long chatId,
//                                                 Date lastChatMessageCreatedAt) {
//        validateChat(matchDAO.findById(accountId, recipientId), identityToken, chatId);
//        List<ChatMessageDTO> chatMessageDTOs = new ArrayList<>();
//        for (ChatMessage chatMessage : chatMessageDAO.findAllByChatIdAfter(chatId, DateUtils.addHours(lastChatMessageCreatedAt, -1))) {
//            ChatMessageDTO chatMessageDTO = modelMapper.map(chatMessage, ChatMessageDTO.class);
//            if (chatMessage.getAccountId().equals(recipientId))
//                chatMessageDTO.setMessageId(null);
//            chatMessageDTOs.add(chatMessageDTO);
//        }
//        return chatMessageDTOs;
//    }


}
