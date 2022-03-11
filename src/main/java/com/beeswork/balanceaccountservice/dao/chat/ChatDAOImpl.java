package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.chat.Chat;
import com.beeswork.balanceaccountservice.entity.chat.QChat;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.UUID;

@Repository
public class ChatDAOImpl extends BaseDAOImpl<Chat> implements ChatDAO {

    private final QChat qChat = QChat.chat;

    @Autowired
    public ChatDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }

    @Override
    public Chat findBy(UUID id) {
        return jpaQueryFactory.selectFrom(qChat).where(qChat.id.eq(id)).fetchFirst();
    }
}
