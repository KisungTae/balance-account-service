package com.beeswork.balanceaccountservice.dto.chat;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ChatMessageDTO {
    private Long   id;
    private String body;
    private Long   messageId;
    private UUID   accountId;
    private UUID   recipientId;
    private Long   chatId;
    private Date   createdAt = new Date();
}
