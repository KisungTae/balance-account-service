package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.QChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.QChatMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class ChatMessageDAOImpl extends BaseDAOImpl<ChatMessage> implements ChatMessageDAO {

    private final QChatMessage qChatMessage = QChatMessage.chatMessage;

    public ChatMessageDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public List<ChatMessageDTO> findAllUnreceived(UUID accountId) {
//        return jpaQueryFactory.select(new QChatMessageDTO(qChatMessage.id,
//                                                          qChatMessage.body,
//                                                          qChatMessage.chat.id,
//                                                          qChatMessage.createdAt))
//                              .from(qChatMessage)
//                              .where(qChatMessage.recipient.id.eq(accountId).and(qChatMessage.received.eq(false)))
//                              .fetch();

        return null;
    }

    @Override
    public List<ChatMessage> findAllIn(List<UUID> chatMessageIds) {
//        return jpaQueryFactory.selectFrom(qChatMessage).where(qChatMessage.id.in(chatMessageIds)).fetch();
        return null;
    }

    @Override
    public ChatMessage findById(UUID chatMessageId) {
        return entityManager.find(ChatMessage.class, chatMessageId);
    }


}
