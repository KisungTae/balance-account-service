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
    private Long    id;
    private UUID    chatId;
    private UUID    senderId;
    private UUID    recipientId;
    private UUID    tag;
    private String  body;
    private boolean firstMessage;
    private Date    createdAt;
    private String  error;
    private String  errorMessage;

    public ChatMessageVM(UUID tag, String error, String errorMessage) {
        this.tag = tag;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public ChatMessageVM(Long id, UUID tag, Date createdAt) {
        this.id = id;
        this.tag = tag;
        this.createdAt = createdAt;
    }

    public boolean isError() {
        return this.error != null;
    }
}
