package com.beeswork.balanceaccountservice.dto.chat;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDTO {
    private Long   id;
    private String body;
    private Long   key;
    private Long   chatId;
    private Date   createdAt;

    @QueryProjection
    public ChatMessageDTO(Long id, Long key, Date createdAt) {
        this.id = id;
        this.key = key;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public ChatMessageDTO(Long id, String body, Long chatId, Date createdAt) {
        this.id = id;
        this.body = body;
        this.chatId = chatId;
        this.createdAt = createdAt;
    }

}
