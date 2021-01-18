package com.beeswork.balanceaccountservice.dao.chatmessage;

import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ChatMessageDAOImpl extends BaseDAOImpl<ChatMessage>  implements ChatMessageDAO {

    public ChatMessageDAOImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(entityManager, jpaQueryFactory);
    }


}
