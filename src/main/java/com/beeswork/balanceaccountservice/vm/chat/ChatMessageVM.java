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
    private UUID    id;
    private String  body;
    private Long    chatId;
    private UUID    accountId;
    private UUID    recipientId;
    private Date    createdAt;
    private String  error;

    public ChatMessageVM(UUID id, String error) {
        this.id = id;
        this.error = error;
    }

    public boolean isError() {
        return this.error != null;
    }
}
