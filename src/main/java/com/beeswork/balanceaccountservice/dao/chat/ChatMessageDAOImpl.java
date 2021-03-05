package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.QChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.QChatMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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
                                                          qChatMessage.chat.id,
                                                          qChatMessage.createdAt))
                              .from(qChatMessage)
                              .where(qChatMessage.recipient.id.eq(accountId).and(qChatMessage.received.eq(false)))
                              .fetch();
    }

    @Override
    public List<ChatMessageDTO> findAllUnfetched(UUID accountId) {
        return jpaQueryFactory.select(new QChatMessageDTO(qChatMessage.id,
                                                          qChatMessage.chat.id,
                                                          qChatMessage.messageId,
                                                          qChatMessage.createdAt))
                              .from(qChatMessage)
                              .where(qChatMessage.account.id.eq(accountId).and(qChatMessage.fetched.eq(false)))
                              .fetch();
    }

    @Override
    public List<ChatMessage> findAllInWithLock(List<Long> chatMessageIds) {
        return entityManager.createQuery("from ChatMessage where id in (:chatMessageIds)", ChatMessage.class)
                            .setParameter("chatMessageIds", chatMessageIds)
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                            .getResultList();
    }

    @Override
    public ChatMessage findByIdWithLock(Long chatMessageId) {
        return entityManager.find(ChatMessage.class, chatMessageId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public ChatMessage findByMessageId(Long messageId) {
        return jpaQueryFactory.selectFrom(qChatMessage).where(qChatMessage.messageId.eq(messageId)).fetchFirst();
    }


}
