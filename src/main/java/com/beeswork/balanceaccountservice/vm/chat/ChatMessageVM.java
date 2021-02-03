package com.beeswork.balanceaccountservice.vm.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ChatMessageVM {
    private Long id;
    private String body;
    private Long messageId;
    private UUID accountId;
    private UUID recipientId;
    private Long chatId;
    private final Date createdAt = new Date();
}
