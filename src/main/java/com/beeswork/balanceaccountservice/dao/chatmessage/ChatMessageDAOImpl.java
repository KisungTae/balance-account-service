package com.beeswork.balanceaccountservice.dao.chatmessage;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.QChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.QChatMessage;
import com.querydsl.core.types.Projections;
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
    public List<ChatMessageDTO> findAllUnreceived(UUID accountId) {
        return jpaQueryFactory.select(new QChatMessageDTO(qChatMessage.id,
                                                          qChatMessage.body,
                                                          qChatMessage.chatId,
                                                          qChatMessage.createdAt))
                              .from(qChatMessage)
                              .where(qChatMessage.recipientId.eq(accountId).and(qChatMessage.received.eq(false)))
                              .fetch();
    }

    @Override
    public List<ChatMessageDTO> findAllSentAfter(UUID accountId, Date chatMessageFetchedAt) {
        return jpaQueryFactory.select(new QChatMessageDTO(qChatMessage.id,
                                                          qChatMessage.messageId,
                                                          qChatMessage.createdAt))
                              .from(qChatMessage)
                              .where(qChatMessage.accountId.eq(accountId)
                                                           .and(qChatMessage.createdAt.after(chatMessageFetchedAt)))
                              .fetch();
    }


}
