package com.beeswork.balanceaccountservice.dao.chatmessage;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.QChatMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class ChatMessageDAOImpl extends BaseDAOImpl<ChatMessage> implements ChatMessageDAO {

    private final QChatMessage qChatMessage = QChatMessage.chatMessage;


    public ChatMessageDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public List<ChatMessage> findAllByChatIdAfter(Long chatId, Date lastChatMessageCreatedAt) {
        return jpaQueryFactory.selectFrom(qChatMessage)
                              .where(qChatMessage.chatId.eq(chatId)
                                                        .and(qChatMessage.createdAt.gt(lastChatMessageCreatedAt)))
                              .fetch();
    }

    @Override
    public List<ChatMessage> findAllReceivedAfter(UUID accountId, Date lastChatMessageCreatedAt) {
        return jpaQueryFactory.selectFrom(qChatMessage)
                              .where(qChatMessage.recipientId.eq(accountId)
                                                             .and(qChatMessage.createdAt.after(lastChatMessageCreatedAt)))
                              .fetch();
    }


}
