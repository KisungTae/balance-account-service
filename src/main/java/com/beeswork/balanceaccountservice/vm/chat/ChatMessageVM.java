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
    private UUID accountId;
    private UUID recipientId;
}
