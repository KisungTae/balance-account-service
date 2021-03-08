package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.QChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.QSentChatMessage;
import com.beeswork.balanceaccountservice.entity.chat.SentChatMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class SentChatMessageDAOImpl extends BaseDAOImpl<SentChatMessage> implements SentChatMessageDAO {

    private final QSentChatMessage qSentChatMessage = QSentChatMessage.sentChatMessage;

    @Autowired
    public SentChatMessageDAOImpl(EntityManager entityManager,
                                  JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public List<ChatMessageDTO> findAllUnfetched(UUID accountId) {
        return jpaQueryFactory.select(new QChatMessageDTO(qSentChatMessage.chatMessageId,
                                                          qSentChatMessage.messageId,
                                                          qSentChatMessage.createdAt))
                              .from(qSentChatMessage)
                              .where(qSentChatMessage.account.id.eq(accountId).and(qSentChatMessage.fetched.eq(false)))
                              .fetch();
    }

    @Override
    public SentChatMessage findByMessageId(long messageId) {
        return jpaQueryFactory.selectFrom(qSentChatMessage)
                              .where(qSentChatMessage.messageId.eq(messageId))
                              .fetchFirst();
    }

    @Override
    public List<SentChatMessage> findAllIn(List<Long> chatMessageIds) {
        return jpaQueryFactory.selectFrom(qSentChatMessage)
                              .where(qSentChatMessage.chatMessageId.in(chatMessageIds))
                              .fetch();
    }

}
