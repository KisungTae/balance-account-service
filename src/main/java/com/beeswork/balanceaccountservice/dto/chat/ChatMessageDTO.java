package com.beeswork.balanceaccountservice.dto.chat;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Getter
@Setter
public class ChatMessageDTO {
    private Long id;

    @NotEmpty
    private String message;

    private String accountId;
    private String recipientId;
    private String chatId;
    private Date createdAt = new Date();
}
