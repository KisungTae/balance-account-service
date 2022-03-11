package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.chat.Chat;

import java.util.UUID;

public interface ChatDAO extends BaseDAO<Chat> {
    Chat findBy(UUID id);
}
