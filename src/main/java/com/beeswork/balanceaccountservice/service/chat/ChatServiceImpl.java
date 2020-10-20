package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dao.chat.ChatDAO;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatInterService {

    private final ChatDAO chatDAO;

    @Autowired
    public ChatServiceImpl(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
    }

    @Override
    public void persist(Chat chat) {
        chatDAO.persist(chat);
    }
}
