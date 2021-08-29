package com.beeswork.balanceaccountservice.vm.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageVM {
    private Long id;
    private String body;
    private Long chatId;
    private UUID accountId;
    private UUID recipientId;
    private Date createdAt = new Date();
    private boolean sent = true;
    private String error;

    public ChatMessageVM(boolean sent, String error) {
        this.sent = sent;
        this.error = error;
    }
}
