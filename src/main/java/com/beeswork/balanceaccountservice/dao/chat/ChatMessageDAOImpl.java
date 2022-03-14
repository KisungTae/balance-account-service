package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class ChatMessageDAOImpl extends BaseDAOImpl<ChatMessage> implements ChatMessageDAO {

    private final QChatMessage        qChatMessage        = QChatMessage.chatMessage;
    private final QChatMessageReceipt qChatMessageReceipt = QChatMessageReceipt.chatMessageReceipt;

    public ChatMessageDAOImpl(EntityManager entityManager,
                              JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public List<ChatMessage> findAllBy(UUID chatId, long lastChatMessageId, int loadSize) {
        return jpaQueryFactory.selectFrom(qChatMessage)
                              .where(qChatMessage.chat.id.eq(chatId).and(qChatMessage.id.lt(lastChatMessageId)))
                              .orderBy(qChatMessage.id.desc())
                              .limit(loadSize)
                              .fetch();
    }

    @Override
    public List<ChatMessage> findAllBy(UUID senderId, UUID chatId, UUID appToken, int startPosition, int loadSize) {
        return entityManager.createNativeQuery("select * from (select * " +
                                               "                  from chat_message " +
                                               "                  where chat_id = :chatId" +
                                               "                  order by id desc " +
                                               "                  limit :limit " +
                                               "                  offset :offset) as sub " +
                                               "left join chat_message_receipt cmr " +
                                               "on sub.id = cmr.chat_message_id " +
                                               "   and cmr.account_id = :accountId " +
                                               "where app_token is null " +
                                               "or app_token != :appToken", ChatMessage.class)
                            .setParameter("chatId", chatId)
                            .setParameter("limit", loadSize)
                            .setParameter("offset", startPosition)
                            .setParameter("accountId", senderId)
                            .setParameter("appToken", appToken)
                            .getResultList();
    }

    @Override
    public ChatMessage findBy(UUID chatId, Long chatMessageId) {
        return jpaQueryFactory.selectFrom(qChatMessage)
                              .where(qChatMessage.chatId.eq(chatId).and(qChatMessage.id.eq(chatMessageId)))
                              .fetchFirst();
    }
}
