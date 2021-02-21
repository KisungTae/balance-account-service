package com.beeswork.balanceaccountservice.dao.chatmessage;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.QChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.QChatMessage;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public List<ChatMessageDTO> findAllUnfetched(UUID accountId) {
        return jpaQueryFactory.select(new QChatMessageDTO(qChatMessage.id,
                                                          qChatMessage.messageId,
                                                          qChatMessage.createdAt))
                              .from(qChatMessage)
                              .where(qChatMessage.accountId.eq(accountId).and(qChatMessage.fetched.eq(false)))
                              .fetch();
    }

    @Override
    public List<ChatMessage> findAllIn(List<Long> chatMessageIds) {
        return jpaQueryFactory.selectFrom(qChatMessage).where(qChatMessage.id.in(chatMessageIds)).fetch();
    }

    @Override
    public ChatMessage findById(Long chatMessageId) {
        return jpaQueryFactory.selectFrom(qChatMessage).where(qChatMessage.id.eq(chatMessageId)).fetchFirst();
    }

    @Override
    public ChatMessage findByMessageId(Long messageId) {
        return jpaQueryFactory.selectFrom(qChatMessage).where(qChatMessage.messageId.eq(messageId)).fetchFirst();
    }


}
