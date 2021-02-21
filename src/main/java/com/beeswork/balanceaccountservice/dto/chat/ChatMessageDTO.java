package com.beeswork.balanceaccountservice.dto.chat;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDTO {
    private Long   id;
    private String body;
    private Long   messageId;
    private Long   chatId;
    private Date   createdAt;

    @QueryProjection
    public ChatMessageDTO(Long id, Long messageId, Date createdAt) {
        this.id = id;
        this.messageId = messageId;
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
