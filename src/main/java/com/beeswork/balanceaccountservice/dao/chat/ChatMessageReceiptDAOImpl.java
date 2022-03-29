package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessageReceipt;
import com.beeswork.balanceaccountservice.entity.chat.QChatMessageReceipt;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Repository
public class ChatMessageReceiptDAOImpl extends BaseDAOImpl<ChatMessageReceipt> implements ChatMessageReceiptDAO {

    private QChatMessageReceipt qChatMessageReceipt = QChatMessageReceipt.chatMessageReceipt;

    @Autowired
    public ChatMessageReceiptDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public ChatMessageReceipt findBy(UUID accountId, UUID chatId, long chatMessageId) {
//        return jpaQueryFactory.selectFrom(qChatMessageReceipt)
//                              .where(qChatMessageReceipt.account.id.eq(accountId)
//                                                                   .and(qChatMessageReceipt.chatId.eq(chatId))
//                                                                   .and(qChatMessageReceipt.chatMessage.id.eq(chatMessageId)))
//                              .fetchFirst();
        return null;
    }

    @Override
    public List<ChatMessageReceipt> findAllBy(UUID accountId, UUID chatId, List<Long> chatMessageIds) {
        return jpaQueryFactory.selectFrom(qChatMessageReceipt)
                              .where(qChatMessageReceipt.accountId.eq(accountId)
                                                                  .and(qChatMessageReceipt.chatId.eq(chatId))
                                                                  .and(qChatMessageReceipt.chatMessageId.in(chatMessageIds))).fetch();
    }
}
