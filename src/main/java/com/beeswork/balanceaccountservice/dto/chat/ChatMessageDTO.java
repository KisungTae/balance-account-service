package com.beeswork.balanceaccountservice.dto.chat;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDTO {
    private PushType pushType = PushType.CHAT_MESSAGE;
    private UUID id;
    private UUID accountId;
    private UUID recipientId;
    private String body;
    private Long key;
    private Long chatId;
    private Date createdAt;

    @QueryProjection
    public ChatMessageDTO(UUID id, Long key, Date createdAt) {
        this.id = id;
        this.key = key;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public ChatMessageDTO(UUID id, String body, Long chatId, Date createdAt) {
        this.id = id;
        this.body = body;
        this.chatId = chatId;
        this.createdAt = createdAt;
    }

}
